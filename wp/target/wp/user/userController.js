app.controller('userController',['$scope', '$window', '$state', 'userService', 'homeService',
	function($scope, $window, $state, userService, homeService){

		function init(){
			$scope.user = undefined;
			$scope.userCopy=undefined;
			$scope.notSeen=0;
			$scope.allMessages=[];
			$scope.messagesForUser=[];			//sadrzi {user,messages,seen}
			$scope.selectedUserMessages=[];
			$scope.selectedUserMessage=undefined;
			$scope.newMessage='';
			$scope.messageSent=false;
			$scope.searchValue='';
			$scope.users=[];
			$scope.searchResult=[];
			$scope.userProfile=undefined;
			$scope.complaintId=undefined;
			$scope.complaintObject={};
			$scope.saved=undefined;
			$scope.comments=undefined;
			$scope.selectedTheme=undefined;
			$scope.selectedComment=undefined;
			var u = JSON.parse($window.localStorage.getItem("user"));
			if(u!=null){
				$scope.user = u; 
				$scope.userCopy=u;
				$scope.repeatedPass=$scope.userCopy.password;
				findAllMessages($scope.user.username);
				findUsers();
				findComplaints();
				findSavedEntities();
				findFollowedForums();
			}else{
				$state.go('home');
			}
		};

		init();

		$scope.initialize=function(){
			init();
		}

		$scope.setNotSeen=function(){
			$scope.notSeen=0;
		}

		
		$scope.logout = function(){
			$window.localStorage.setItem("user", null);
			init();
			$state.go('home');
		}
		
		function findFollowedForums(){
			var forums=[];
			for(fId in $scope.user.forums){
				userService.findOneForum($scope.user.forums[fId].name).then(
						function(response){
							forums.push(response.data);
						}
				);
			}
			$scope.user.forums=forums;
			$scope.userCopy.forums=forums;
		}
		
		function findSavedEntities(){
			var themes=[];
			for(themeId in $scope.user.themes){
				userService.findOneTheme($scope.user.themes[themeId].id).then(
						function(response){
							var theme=response.data;
							theme.saveTheme=false;
							themes.push(theme);
						}
				);
			}
			$scope.user.themes=themes;
			$scope.userCopy.themes=themes;
			
			var comments=[];
			for(comId in $scope.user.comments){
				userService.findOneComment($scope.user.comments[comId].id).then(
						function(response){
							var comment=response.data;
							comment.saveComment=false;
							comments.push(comment);
						}
				);
			}
			$scope.user.comments=comments;
			$scope.userCopy.themes=themes;
		}
		
		function findComplaints(){
			var complaints=[];
			for(cId in $scope.user.complaints){
				userService.findOneComplaint($scope.user.complaints[cId].id).then(
					function(response){
						var complaint=response.data;
						if(complaint.comment!=null){
							userService.findOneComment(complaint.comment.id).then(
									function(response1){
										complaint.comment=response1.data;
									}
							);
						}
						complaints.push(complaint);
					}
				);
			}
			$scope.user.complaints=complaints;
			$scope.userCopy.complaints=complaints;
		}
		
		function findAllMessages(username){
			$scope.allMessages=[];
			userService.findAllMessagesForUser(username).then(
					function(response){
						$scope.allMessages=response.data;
						sortMessages();
					}
			);
		};
		
		function sortMessages(){
			$scope.messagesForUser=[];
			for(msgId in $scope.allMessages){
				var isInList=false;
				for(userMsgId in $scope.messagesForUser){
					if($scope.messagesForUser[userMsgId].user.username==$scope.allMessages[msgId].sender.username
							|| $scope.messagesForUser[userMsgId].user.username==$scope.allMessages[msgId].recipient.username){
						isInList=true;
						$scope.messagesForUser[userMsgId].messages.push($scope.allMessages[msgId]);
						if($scope.allMessages[msgId].seen==false 
								&& $scope.allMessages[msgId].recipient.username==$scope.user.username){
							$scope.messagesForUser[userMsgId].seen=false;
						}
					}
				}
				if(isInList==false){
					var newObject={};
					if($scope.allMessages[msgId].sender.username!=$scope.user.username)
						newObject.user=$scope.allMessages[msgId].sender;
					else
						newObject.user=$scope.allMessages[msgId].recipient;
					newObject.messages=[];
					newObject.messages.push($scope.allMessages[msgId]);
					if($scope.allMessages[msgId].recipient.username==$scope.user.username)
						newObject.seen=$scope.allMessages[msgId].seen;
					else
						newObject.seen=true;
					$scope.messagesForUser.push(newObject);
				}
			}
			countUnseenMessages();
		}
		
		function countUnseenMessages(){
			$scope.notSeen=0;
			$scope.selectedUserMessages=[];
			for(userMsgId in $scope.messagesForUser){
				if($scope.messagesForUser[userMsgId].seen==false)
					$scope.notSeen+=1;
				if($scope.selectedUserMessages!=undefined && $scope.selectedUserMessage!=undefined
						&& $scope.messagesForUser[userMsgId].user.username==$scope.selectedUserMessage.username)
					$scope.selectedUserMessages=$scope.messagesForUser[userMsgId].messages;
			}
		}
		
		$scope.getMessagesForUser = function(user){
			$scope.searchValue='';
			$scope.selectedUserMessage=user;
			for(userMsgId in $scope.messagesForUser){
				if($scope.messagesForUser[userMsgId].user.username==$scope.selectedUserMessage.username){
					if($scope.messagesForUser[userMsgId].seen==false){
						for(msgId in $scope.messagesForUser[userMsgId].messages){
							if($scope.messagesForUser[userMsgId].messages[msgId].seen==false){
								$scope.messagesForUser[userMsgId].messages[msgId].seen=true;
								updateMessage($scope.messagesForUser[userMsgId].messages[msgId]);
							}
							if(msgId==$scope.messagesForUser.length-1)
								findAllMessages($scope.user.username);
						}
					}else
						findAllMessages($scope.user.username);
				}
			}
		};
		
		
		function updateMessage(message){
			userService.updateMessage(message).then(
					function(response){

					},function(){
						alert('Došlo je do greške prilikom ažuriranja poruke.');
					}
			);
		};
		
			
		$scope.back = function(backId){
			if(backId=='message'){
				$scope.selectedUserMessages=[];
				$scope.selectedUserMessage=undefined;
				findAllMessages($scope.user.username);
				$scope.newMessage='';
				$scope.messageSent=false;
				$scope.searchValue='';
			}else if(backId=='user'){
				$scope.userProfile=undefined;
			}else if(backId=='complaint'){
				$scope.complaintId=undefined;
				$scope.complaintObject={};
			}else if(backId=='comment'){
				$scope.saved=undefined;
				$scope.comments=undefined;
				$scope.selectedTheme=undefined;
				$scope.selectedComment=undefined;
				$scope.saveTheme=true;
			}
		};
		
		$scope.sendMessage = function(){
			if($scope.newMessage!=''){
				var message={};
				message.sender=$scope.user;
				message.recipient=$scope.selectedUserMessage;
				message.content=$scope.newMessage;
				message.seen=false;
				userService.saveMessage(message).then(
						function(response){
							$scope.newMessage='';
							$scope.messageSent=true;
							findAllMessages($scope.user.username);
						},function(){
							alert('Došlo je do greške prilikom slanja poruke.');
						}
				);
			}
		} 
		
		$scope.search = function(searchId,user){
			if(searchId=='users'){
				if($scope.searchValue!=''){
					if(user==null){
						$scope.searchResult=[];
						for(userId in $scope.users){
							if($scope.users[userId].username.indexOf($scope.searchValue)!=-1){
								$scope.searchResult.push($scope.users[userId]);
							}
						}
						if($scope.searchResult.length==1){
							$scope.selectedUserMessage=$scope.searchResult[0];
							$scope.searchResult=[];
							$scope.getMessagesForUser($scope.selectedUserMessage);
						}
					}else{
						$scope.selectedUserMessage=user;
						$scope.searchResult=[];
						$scope.getMessagesForUser($scope.selectedUserMessage);
					}
				}else
					findAllMessages($scope.user.username);
			}
		}
		
		function findUsers(){
			homeService.findAllUsers().then(
					function(response){
						$scope.users=response.data;
					}
			);
		}
		

		$scope.findUser = function(username){
			homeService.findOneUser(username).then(
					function(response){
						$scope.userProfile=response.data;
					},function(){
						alert('Korisnik nije nadjen.');
					}
			);
		};
		
		$scope.seeComplaint = function(cId,obj){
			$scope.complaintId=cId;
			$scope.complaintObject=obj;
			if(cId=='forum'){
				userService.findOneForum(obj.forum.name).then(
						function(response){
							$scope.complaintObject.forum=response.data;
						}
				);
			}else if(cId=='theme'){
				userService.findOneTheme(obj.theme.id).then(
						function(response){
							$scope.complaintObject.theme=response.data;
						}
				);
			}else if(cId=='comment'){
				$scope.complaintObject.comment=obj.comment;
			}
		};
		
		
		$scope.deleteEntity = function(){
			if($scope.complaintId=='forum'){
				$scope.newMessage="Na Vaš forum: '"
					+$scope.complaintObject.forum.name+"' je poslata žalba i taj forum je obrisan.";
				$scope.selectedUserMessage=$scope.complaintObject.forum.manager;
				$scope.sendMessage();
				
				$scope.newMessage="Žalba na forum: '"
					+$scope.complaintObject.forum.name
					+"' je pregledana. Forum je obrisan.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
				
				
				$scope.newMessage='';
				$scope.selectedUserMessage=undefined;
				homeService.deleteForum($scope.complaintObject.forum.name).then(
					function(){
						userService.deleteComplaint($scope.complaintObject.id).then(
								function(response){
									homeService.findOneUser($scope.user.username).then(
											function(response1){
												$window.localStorage.setItem("user", JSON.stringify(response1.data));
												$scope.user=response1.data;
												$scope.userCopy=response1.data;
												findAllMessages($scope.user.username);
												findUsers();
												findComplaints();
											}
									);
								}
						);
						alert('Forum je obrisan.');
						$scope.back('complaint');
					}	
				);
			}else if($scope.complaintId=='theme'){
				$scope.newMessage="Na Vašu temu: '"
					+$scope.complaintObject.theme.title+"' je poslata žalba i ta tema je obrisana.";
				$scope.selectedUserMessage=$scope.complaintObject.theme.author;
				$scope.sendMessage();
				
				$scope.newMessage="Žalba na temu: '"
					+$scope.complaintObject.theme.title
					+"' je pregledana. Tema je obrisana.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
				
				
				$scope.newMessage='';
				$scope.selectedUserMessage=undefined;
				homeService.deleteTheme($scope.complaintObject.theme.id).then(
					function(){
						userService.deleteComplaint($scope.complaintObject.id).then(
								function(response){
									homeService.findOneUser($scope.user.username).then(
											function(response1){
												$window.localStorage.setItem("user", JSON.stringify(response1.data));
												$scope.user=response1.data;
												$scope.userCopy=response1.data;
												findAllMessages($scope.user.username);
												findUsers();
												findComplaints();
											}
									);
								}
						);
						alert('Tema je obrisana.');
						$scope.back('complaint');
					}	
				);
			}else if($scope.complaintId=='comment'){
				$scope.newMessage="Dobili smo žalbu na Vaš komentar: '"
					+$scope.complaintObject.comment.text+"' unutar teme: '"
					+$scope.complaintObject.comment.theme.id+"'. Taj komentar je obrisan.";
				$scope.selectedUserMessage=$scope.complaintObject.comment.author;
				$scope.sendMessage();
				
				$scope.newMessage="Žalba na komentar: '"
					+$scope.complaintObject.comment.text
					+"' je pregledana. Komentar je obrisan.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
				
				
				$scope.newMessage='';
				$scope.selectedUserMessage=undefined;
				homeService.deleteComment($scope.complaintObject.comment.id).then(
					function(){
						userService.deleteComplaint($scope.complaintObject.id).then(
								function(response){
									homeService.findOneUser($scope.user.username).then(
											function(response1){
												$window.localStorage.setItem("user", JSON.stringify(response1.data));
												$scope.user=response1.data;
												$scope.userCopy=response1.data;
												findAllMessages($scope.user.username);
												findUsers();
												findComplaints();
											}
									);
								}
						);
						alert('Komentar je obrisan.');
						$scope.back('complaint');
					}	
				);
			}
		};
		
		$scope.complaintWarn = function(){
			if($scope.complaintId=='forum'){
				$scope.newMessage="Dobili smo žalbu na forum koji ste Vi kreirali. Naziv foruma: '"
					+$scope.complaintObject.forum.name+"'.";
				$scope.selectedUserMessage=$scope.complaintObject.forum.manager;
				$scope.sendMessage();
				
				$scope.newMessage="Žalba na forum: '"
					+$scope.complaintObject.forum.name
					+"' je pregledana. Poslato je upozorenje autoru foruma.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
				
				
				$scope.newMessage='';
				$scope.selectedUserMessage=undefined;
				userService.deleteComplaint($scope.complaintObject.id).then(
						function(response){
							homeService.findOneUser($scope.user.username).then(
									function(response1){
										$window.localStorage.setItem("user", JSON.stringify(response1.data));
										$scope.user=response1.data;
										$scope.userCopy.response1.data;
										findAllMessages($scope.user.username);
										findUsers();
										findComplaints();
									}
							);
						}
				);
				alert('Poruke za upozorenje su poslate.');
				$scope.back('complaint');
			}else if($scope.complaintId=='theme'){
				$scope.newMessage="Dobili smo žalbu na temu koju ste Vi kreirali. Naziv teme: '"
					+$scope.complaintObject.theme.title+"'.";
				$scope.selectedUserMessage=$scope.complaintObject.theme.author;
				$scope.sendMessage();
				
				$scope.newMessage="Žalba na temu: '"
					+$scope.complaintObject.theme.title
					+"' je pregledana. Poslato je upozorenje autoru teme.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
				
				
				$scope.newMessage='';
				$scope.selectedUserMessage=undefined;
				findAllMessages($scope.user.username);
				userService.deleteComplaint($scope.complaintObject.id).then(
						function(response){
							homeService.findOneUser($scope.user.username).then(
									function(response1){
										//$scope.user=response1.data;
										$window.localStorage.setItem("user", JSON.stringify(response1.data));
										$scope.user=response1.data;
										$scope.userCopy=response1.data;
										findAllMessages($scope.user.username);
										findUsers();
										findComplaints();
									}
							);
						}
				);
				alert('Poruke za upozorenje su poslate.');
				$scope.back('complaint');
			}else if($scope.complaintId=='comment'){
				$scope.newMessage="Dobili smo žalbu na Vaš komentar: '"
					+$scope.complaintObject.comment.text+"' unutar teme: '"
					+$scope.complaintObject.comment.theme.id+"'.";
				$scope.selectedUserMessage=$scope.complaintObject.comment.author;
				$scope.sendMessage();
				
				$scope.newMessage="Žalba na komentar: '"
					+$scope.complaintObject.comment.text
					+"' je pregledana. Poslato je upozorenje autoru komentara.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
				
				
				$scope.newMessage='';
				$scope.selectedUserMessage=undefined;
				findAllMessages($scope.user.username);
				userService.deleteComplaint($scope.complaintObject.id).then(
						function(response){
							homeService.findOneUser($scope.user.username).then(
									function(response1){
										$window.localStorage.setItem("user", JSON.stringify(response1.data));
										$scope.user=response1.data;
										$scope.userCopy=response1.data;
										findAllMessages($scope.user.username);
										findUsers();
										findComplaints();
									}
							);
						}
				);
				alert('Poruke za upozorenje su poslate.');
				$scope.back('complaint');
			}
		};
		
		$scope.complaintDecline = function(){
			if($scope.complaintId=='forum'){
				$scope.newMessage="Žalba na forum: '"
					+$scope.complaintObject.forum.name
					+"' je odbijena.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
			}else if($scope.complaintId=='theme'){
				$scope.newMessage="Žalba na temu: '"
					+$scope.complaintObject.theme.title
					+"' je odbijena.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
				
			}else if($scope.complaintId=='comment'){
				$scope.newMessage="Žalba na komentar: '"
					+$scope.complaintObject.comment.text
					+"' je odbijena.";
				$scope.selectedUserMessage=$scope.complaintObject.user;
				$scope.sendMessage();
				
			}
			userService.deleteComplaint($scope.complaintObject.id).then(
					function(response){
						homeService.findOneUser($scope.user.username).then(
								function(response1){
									$window.localStorage.setItem("user", JSON.stringify(response1.data));
									$scope.user=response1.data;
									$scope.userCopy=response1.data;
									findAllMessages($scope.user.username);
									findUsers();
									findComplaints();
								}
						);
					}
			);
			$scope.newMessage='';
			$scope.selectedUserMessage=undefined;

			alert('Žalba je odbijena.');
			$scope.back('complaint');
		};
		
		$scope.getTheme = function(themeId){
			$scope.saved=true;
			homeService.findCommentsByTheme(themeId).then(
					function(response){
						$scope.comments = response.data.comments;
						$scope.selectedTheme = response.data;
						for(comId in $scope.comments){
							$scope.comments[comId].subCommentsVisible=false;
						}
						if($scope.user!=undefined){
							for(themeId in $scope.user.themes){
								if($scope.user.themes[themeId].id==$scope.selectedTheme.id){
									$scope.saveTheme=false;
									break;
								}
							}
						}
					},
					function(){
						alert('Tema nije nadjena.');
					}
			);
		};
		
		

		$scope.showSubcomments = function(commentId){
			homeService.findSubcommentsByComment(commentId).then(
					function(response){
						for(comId in $scope.comments){
							if(angular.equals($scope.comments[comId].id,commentId)){
								$scope.comments[comId].subComments=response.data;
								$scope.comments[comId].subCommentsVisible=true;
								break;
							}
						}
					},function(){
						alert('Došlo je do greške priliko traženja komentara.');
					}
			);
		}
		
		$scope.hideSubcomments = function(commentId){
			for(comId in $scope.comments){
				if(angular.equals($scope.comments[comId].id,commentId)){
					$scope.comments[comId].subCommentsVisible=false;
					break;
				}
			}	
		};
		
		$scope.getComment = function(commentId){
			$scope.saved=true;
			userService.findOneComment(commentId).then(
					function(response){
						$scope.selectedComment=response.data;
					},function(){
						alert('Komentar nije nađen.');
					}
			);
		}
		
		$scope.removeSaved = function(saveId,obj){
			if(saveId=='theme'){
				for(themeId in $scope.user.themes){
					if($scope.user.themes[themeId].id==obj.id){
						$scope.user.themes.splice(themeId,1);
						$scope.userCopy.themes.splice(themeId,1);
						break;
					}
				}
				homeService.updateUser($scope.user).then(
						function(response){
							alert('Tema je obrisana iz liste sačuvanih tema.');
							$window.localStorage.setItem("user", JSON.stringify(response.data));
							$scope.back('comment');
						},function(){
							$scope.user.themes.push(obj);
							$scope.userCopy.themes.push(obj);
							alert('Došlo je do greške prilikom brisanja teme.');
						}
				);
			}else if(saveId=='comment'){
				for(comId in $scope.user.comments){
					if($scope.user.comments[comId].id==obj.id){
						$scope.user.comments.splice(comId,1);
						$scope.userCopy.comments.splice(comId,1);
						break;
					}
				}
				homeService.updateUser($scope.user).then(
						function(response){
							alert('Komentar je obrisan iz liste sačuvanih komentara.')
							$window.localStorage.setItem("user", JSON.stringify(response.data));
							$scope.back('comment');
						},function(){
							$scope.user.comments.push(obj);
							$scope.userCopy.comments.push(obj);
							alert('Došlo je do greške prilikom brisanja komentara.');
						}
				);
			}
		}
		
		
		$scope.updateUser = function(){
			if($scope.repeatedPass==$scope.userCopy.password){
				homeService.updateUser($scope.userCopy).then(
						function(response){
							alert('Podaci su promenjeni.');
							$window.localStorage.setItem("user", JSON.stringify(response.data));
						},function(){
							alert('Došlo je do greške.');
						}
				);
			}else{
				alert('Niste dobro uneli šifru.');
			}
		}
		
		
		$scope.cancelUpdate = function(){
			init();
		}
		
		
		
		
}]);