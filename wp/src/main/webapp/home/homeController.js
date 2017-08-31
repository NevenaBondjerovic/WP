app.controller('homeController',['$scope', '$window', '$state', 'homeService', 'userService',
	function($scope, $window, $state, homeService,userService){

		function init(){
			$scope.user = undefined;
			$scope.notSeen=0;
			var u = JSON.parse($window.localStorage.getItem('user'));
			if(u!=null){
				$scope.user = u; 
				findAllMessages($scope.user.username);
			}

			$scope.managersAndAdmins=[];
			$scope.allUsers=[];
			findAllUsers();
			
			$scope.themes = undefined;
			$scope.allThemes=undefined;
			$scope.comments = undefined;
			$scope.selectedTheme = undefined;
			$scope.userProfile = undefined;
			$scope.forumName=undefined;
			$scope.forum={};
			$scope.isManager=false;
			
			if($state.current.name=="user.followed"){
				findFollowedForums();
			}else{
				findAllForums();
			}
			$scope.commentTextbox=0;
			$scope.commentTheme={};
			$scope.commentTheme.comment='';
			$scope.commentTheme.parent=undefined;
			$scope.updateState=false;
			$scope.createId=undefined;
			$scope.newObject=undefined;
			$scope.searchValue="";
			$scope.searchValueAll="";
			$scope.searchMode=false;
			$scope.allSearchResult={};
			$scope.allSearchResult.themes=[];
			$scope.allSearchResult.users=[];
			$scope.allSearchResult.forums=[];
			$scope.saveTheme=true;
			$scope.saveForum=true;
			$scope.chosenFile=undefined;
			
			
		};

		init();
		
		$scope.initialize=function(){
			init();
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
		
		function findFollowedForums(){
			var forums=[];
			for(fId in $scope.user.forums){
				userService.findOneForum($scope.user.forums[fId].name).then(
						function(response){
							forums.push(response.data);
						}
				);
			}
			$scope.forums=forums;
			$scope.allForums=forums;
			
		}
		
		function findAllForums(){
			homeService.findAllForums().then(
					function(response){
						$scope.forums =  response.data;
						$scope.allForums=response.data;
					}
			);
		};
		
		function findAllUsers(){
			homeService.findAllUsers().then(
					function(response){
						var users=response.data;
						$scope.allUsers=response.data;
						for(userIndex in users){
							if(users[userIndex].role=="ADMINISTRATOR" 
								|| users[userIndex].role=="MANAGER"){
								users[userIndex].added=false;
								$scope.managersAndAdmins.push(users[userIndex]);
							}
						}
					}
			);
		};
		
		$scope.findThemes = function(forumName){
			$scope.searchValue="";
			$scope.forumName=forumName;
			homeService.findThemesByForum(forumName).then(
					function(response){
						if($scope.searchMode==true)
							$scope.searchMode=false;
						$scope.themes = response.data;
						$scope.allThemes=response.data;
					}
			);
			
			for(forumId in $scope.forums){
				if($scope.forums[forumId].name==forumName){
					$scope.forum=$scope.forums[forumId];
					if($scope.user!=undefined){
						for(managerId in $scope.forum.managers){
							if($scope.user.username==$scope.forum.managers[managerId].username)
								$scope.isManager=true;
						}
					}
					break;
				}
			}
			if($scope.user!=undefined){
				for(fId in $scope.user.forums){
					if($scope.user.forums[fId].name==$scope.forumName){
						$scope.saveForum=false;
						break;
					}
				}
			}
		};
		
		$scope.back = function(backId){
			if(backId=="theme"){
				$scope.themes=undefined;
				$scope.allThemes=undefined;
				$scope.isManager=false;
				$scope.saveForum=true;
				if($state.current.name=="user.followed")
					findFollowedForums();
				else		
					findAllForums();
			}else if(backId=="comment"){
				$scope.selectedTheme=undefined;
				$scope.comments = undefined;
				if(!($scope.allSearchResult!={} && $scope.searchValueAll!=""))
					$scope.findThemes($scope.forumName);
				$scope.saveTheme=true;
			}else if(backId=="user"){
				$scope.userProfile = undefined;
			}else if(backId="create"){
				$scope.createId=undefined;
				$scope.newObject=undefined;
				$scope.chosenFile=undefined;
			}else if(backId="create"){
				$scope.newObject=undefined;
			}
			$scope.searchValue="";
			$scope.createId=undefined;
			$scope.commentTextbox=0;
			$scope.commentTheme.comment='';
			$scope.commentTheme.parent=undefined;

			if($scope.allSearchResult!={} && $scope.searchValueAll!="" && $scope.themes==undefined){
				$scope.search('all');
				$scope.searchMode=true;
			}
		};
		
		
		$scope.getTheme = function(themeId){
			$scope.searchValue="";
			homeService.findCommentsByTheme(themeId).then(
					function(response){
						$scope.comments = response.data.comments;
						$scope.selectedTheme = response.data;
						for(comId in $scope.comments){
							$scope.comments[comId].subCommentsVisible=false;
							$scope.comments[comId].vote=true;
							if($scope.user!=undefined){
								for(userId in $scope.comments[comId].likeUsers){
									if(angular.equals($scope.comments[comId].likeUsers[userId].username,$scope.user.username)){
										$scope.comments[comId].vote=false;
										break;
									}
								}
								for(userId in $scope.comments[comId].dislikeUsers){
									if(angular.equals($scope.comments[comId].dislikeUsers[userId].username,$scope.user.username)){
										$scope.comments[comId].vote=false;
										break;
									}
								}
								for(cId in $scope.user.comments){
									if($scope.user.comments[cId].id==$scope.comments[comId].id){
										$scope.comments[comId].saveComment=false;
									}
								}
							}
							if($scope.comments[comId].saveComment!=false){
								$scope.comments[comId].saveComment=true;
							}
						}
						$scope.selectedTheme.vote=true;
						if($scope.user!=undefined){
							for(userId in $scope.selectedTheme.likeUsers){
								if(angular.equals($scope.selectedTheme.likeUsers[userId].username,$scope.user.username)){
									$scope.selectedTheme.vote=false;
									break;
								}
							}
							for(userId in $scope.selectedTheme.dislikeUsers){
								if(angular.equals($scope.selectedTheme.dislikeUsers[userId].username,$scope.user.username)){
									$scope.selectedTheme.vote=false;
									break;
								}
							}
							for(themeId in $scope.user.themes){
								if($scope.user.themes[themeId].id==$scope.selectedTheme.id){
									$scope.saveTheme=false;
									break;
								}
							}
						}
						if($scope.searchMode==true)
							$scope.searchMode=false;
					},
					function(){
						alert('Tema nije nadjena.');
					}
			);
		};

		$scope.logout = function(){
			$window.localStorage.setItem("user", null);
			init();
			$state.go('home');
		}
		
		
		$scope.showSubcomments = function(commentId){
			homeService.findSubcommentsByComment(commentId).then(
					function(response){
						for(comId in $scope.comments){
							if(angular.equals($scope.comments[comId].id,commentId)){
								$scope.comments[comId].subComments=response.data;
								$scope.comments[comId].subCommentsVisible=true;
								for(subComId in $scope.comments[comId].subComments){
									$scope.comments[comId].subComments[subComId].vote=true;
									if($scope.user!=undefined){
										for(userId in $scope.comments[comId].subComments[subComId].likeUsers){
											if(angular.equals($scope.comments[comId].subComments[subComId].likeUsers[userId].username,$scope.user.username)){
												$scope.comments[comId].subComments[subComId].vote=false;
												break;
											}
										}
										for(userId in $scope.comments[comId].subComments[subComId].dislikeUsers){
											if(angular.equals($scope.comments[comId].subComments[subComId].dislikeUsers[userId].username,$scope.user.username)){
												$scope.comments[comId].subComments[subComId].vote=false;
												break;
											}
										}
										for(cId in $scope.user.comments){
											if($scope.user.comments[cId].id==$scope.comments[comId].subComments[subComId].id){
												$scope.comments[comId].subComments[subComId].saveComment=false;
											}
										}
									}
									if($scope.comments[comId].subComments[subComId].saveComment!=false){
										$scope.comments[comId].subComments[subComId].saveComment=true;
									}
								}
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
		
		$scope.findUser = function(username){
			homeService.findOneUser(username).then(
					function(response){
						if($scope.searchMode==true)
							$scope.searchMode=false;
						$scope.userProfile=response.data;
					},function(){
						alert('Korisnik nije nadjen.');
					}
			);
		};
		
		$scope.vote = function(voteId,likeDislike,selectedObject){
			if(voteId=='theme'){
				if(likeDislike=="like"){
					$scope.selectedTheme.likes=$scope.selectedTheme.likes+1;
					$scope.selectedTheme.likeUsers.push($scope.user);
					$scope.selectedTheme.vote=false;
					updateTheme();
				}else if(likeDislike=="dislike"){
					$scope.selectedTheme.dislikes=$scope.selectedTheme.dislikes+1;
					$scope.selectedTheme.dislikeUsers.push($scope.user);
					$scope.selectedTheme.vote=false;
					updateTheme();
				}
			}else if(voteId=='comment'){
				if(likeDislike=='like'){
					selectedObject.likes=selectedObject.likes+1;
					selectedObject.likeUsers.push($scope.user);
					selectedObject.vote=false;
					updateComment(selectedObject);
				}else if(likeDislike=='dislike'){
					selectedObject.dislikes=selectedObject.dislikes+1;
					selectedObject.dislikeUsers.push($scope.user);
					selectedObject.vote=false;
					updateComment(selectedObject);
				}
			}
		};

		$scope.changeTheme = function(){
			if($scope.newObject!=undefined && $scope.newObject.title!=undefined 
					&& $scope.newObject.type!=undefined && $scope.newObject.text!=undefined){
				$scope.selectedTheme=$scope.newObject;
				if($scope.newObject.type=="PICTURE"){
					if($scope.chosenFile!=undefined){
						$scope.selectedTheme.content=$scope.chosenFile;
						updateTheme();

						if($scope.newObject.text.trim()==""){
							$scope.newObject.text="";
						}else{
							updateTheme();

							alert('Uspešno ste izmenili temu.');
							$scope.newObject=undefined;
							$scope.getTheme($scope.selectedTheme.id);
							$scope.back('create');
						}
					}
				}else if($scope.newObject.type=="LINK"){
					if($scope.newObject.link!=undefined){
						//$scope.newObject.chosenFile=document.getElementById("themePicture").value;
						$scope.selectedTheme.content=$scope.newObject.link;
						updateTheme();

						if($scope.newObject.text.trim()==""){
							$scope.newObject.text="";
						}else{
							updateTheme();

							alert('Uspešno ste izmenili temu.');
							$scope.newObject=undefined;
							$scope.getTheme($scope.selectedTheme.id);
							$scope.back('create');
						}
					}
				}else{
					//$scope.newObject.chosenFile=document.getElementById("themePicture").value;
					$scope.selectedTheme.content=null;
					updateTheme();

					if($scope.newObject.text.trim()==""){
						$scope.newObject.text="";
					}else{
						updateTheme();

						alert('Uspešno ste izmenili temu.');
						$scope.newObject=undefined;
						$scope.getTheme($scope.selectedTheme.id);
						$scope.back('create');
					}
				}
			}
		};
		
		function updateTheme(){
			var theme = {};
			theme.id = $scope.selectedTheme.id;
			theme.forum = $scope.selectedTheme.forum;
			theme.title = $scope.selectedTheme.title;
			theme.type = $scope.selectedTheme.type;
			theme.author = $scope.selectedTheme.author;
			theme.comments = $scope.selectedTheme.comments;
			theme.content = $scope.selectedTheme.content;
			theme.text=$scope.selectedTheme.text;
			theme.date = $scope.selectedTheme.date;
			theme.likes = $scope.selectedTheme.likes;
			theme.dislikes = $scope.selectedTheme.dislikes;
			theme.likeUsers = $scope.selectedTheme.likeUsers;
			theme.dislikeUsers = $scope.selectedTheme.dislikeUsers;
			homeService.updateTheme(theme).then(
					function(response){

					},function(){
						alert('Došlo je do greške.');
					}
			);
		};
		
		function updateComment(comment){
			var comm={};
			comm.id=comment.id;
			comm.theme=comment.theme;
			comm.author=comment.author;
			comm.date=comment.date;
			comm.parentComment=comment.parentComment;
			comm.subComments=comment.subComments;
			comm.text=comment.text;
			comm.likes=comment.likes;
			comm.dislikes=comment.dislikes;
			comm.commentChanged=comment.commentChanged;
			comm.likeUsers=comment.likeUsers;
			comm.dislikeUsers=comment.dislikeUsers;
			homeService.updateComment(comm).then(
					function(response){
						for(comId in $scope.comments){
							if(angular.equals($scope.comments[comId].id,comment.id)){
								$scope.comments[comId]=comment;
							}
						}
					},function(){
						alert('Došlo je do greške prilikom glasanja.');
					}
			);
		};
		
		
		//textboxId
		//1->komentarisi temu
		//2->komentarisi komentar
		//3->izmeni komentar
		//4->izmeni podkomentar
		//5->izmeni temu
		$scope.showComment = function(textboxId,parent){
			$scope.commentTextbox=textboxId;
			$scope.commentTheme.parent=parent;
			if($scope.commentTextbox==3 || $scope.commentTextbox==4){
				$scope.commentTheme.comment=parent.text;
			}
			if($scope.commentTextbox==5){
				$scope.newObject=parent;
				if(parent.type=="PICTURE"){
					$scope.chosenFile=parent.content;
				}else if(parent.type=="LINK"){
					$scope.newObject.link=parent.content;
				}
			}
		};
		
		
		$scope.sendComment = function(parentComment){
			if($scope.commentTheme.comment=="")
				alert("Niste uneli komentar.")
			else{
				var com={};
				com.id=null;
				com.theme=$scope.selectedTheme;
				com.author=$scope.user;
				com.date=null;
				com.parentComment=parentComment;
				com.subComments=null;
				com.text=$scope.commentTheme.comment;
				com.likes=0;
				com.dislikes=0;
				com.commentChanged=false;
				com.likeUsers=null;
				com.dislikeUsers=null;
				homeService.saveComment(com).then(
						function(response){
							var newCom=response.data;
							newCom.subCommentsVisible=false;
							newCom.vote=true;
							$scope.commentTextbox=0;
							$scope.commentTheme.comment="";
							$scope.commentTheme.parent=undefined;
							newCom.saveComment=true;
							if(parentComment==null){
								$scope.selectedTheme.comments.push(newCom);
								updateTheme();
							}else{
								for(comIndex in $scope.selectedTheme.comments){
									if($scope.selectedTheme.comments[comIndex].id==parentComment.id){
										$scope.selectedTheme.comments[comIndex].subComments.push(newCom);
										updateComment($scope.selectedTheme.comments[comIndex]);
										break;
									}
								}
							}
						},function(){
							alert('Došlo je do greške prilikom komentarisanja.');
						}
				);
			}
		};
		
		
		$scope.deleteComment = function(comment){
			if(comment!=null){
				homeService.deleteComment(comment.id).then(
						function(){
							if(comment.parentComment!=null){
								for(comId in $scope.selectedTheme.comments){
									if($scope.selectedTheme.comments[comId].id==comment.parentComment.id){
										for(subComId in $scope.selectedTheme.comments[comId].subComments){
											if($scope.selectedTheme.comments[comId].subComments[subComId].id==comment.id){
												$scope.selectedTheme.comments[comId].subComments.splice(subComId,1);
											}
										}
									}
								}
							}else{
								for(comId in $scope.selectedTheme.comments){
									if($scope.selectedTheme.comments[comId].id==comment.id){
										$scope.selectedTheme.comments.splice(comId,1);
									}
								}
							}
							alert('Komentar je obrisan.');
						},function(){
							alert('Došlo je do greške prilikom brisanja komentara.')
						}
				);
			}
		};
		
		
		$scope.changeComment = function(comment){
			comment.text=$scope.commentTheme.comment;
			if(!$scope.isManager){
				comment.commentChanged=true;
			}
			updateComment(comment);
			$scope.commentTextbox=0;
			$scope.commentTheme.comment="";
			$scope.commentTheme.parent=undefined;
		};
		
		
		$scope.showCreate = function(showId){
			$scope.newObject={};
			$scope.createId=showId;
			if(showId=='theme'){
				$scope.newObject.type="TEXT";
			}else if(showId=='role'){
				$scope.newObject=$scope.userProfile;
			}
		};
		
		$scope.showComplaint = function(showId,complaintOn,obj){
			$scope.newObject={};
			$scope.createId=showId;
			$scope.newObject.user=$scope.user;
			if(complaintOn=='forum'){
				$scope.newObject.forum=obj;
			}else if(complaintOn=='theme'){
				$scope.newObject.theme=obj;
			}else if(complaintOn=='comment')
				$scope.newObject.comment=obj;
			
		};
		
		$scope.sendComplaint = function(){
			if($scope.newObject!=undefined && $scope.newObject.text!=undefined 
					&& $scope.newObject.user!=undefined 
					&& ($scope.newObject.forum!=undefined || $scope.newObject.theme!=undefined 
							|| $scope.newObject.comment!=undefined)){
				homeService.saveComplaint($scope.newObject).then(
						function(response){
							alert('Žalba je poslata.');
							$scope.back('create');
						},function(){
							alert('Došlo je do greške prilikom slanja žalbe.');
						}
				);
			} 
		};
		
		
		$scope.saveThemeFunction = function(){
			if($scope.newObject!=undefined && $scope.newObject.title!=undefined 
					&& $scope.newObject.type!=undefined && $scope.newObject.text!=undefined){
				if($scope.newObject.type=="PICTURE"){
					if($scope.chosenFile!=undefined){
						saveNewTheme();
					}
				}else if($scope.newObject.type=="LINK"){
					if($scope.newObject.link!=undefined){
						saveNewTheme();
					}
				}else{
					saveNewTheme();
				}
			}
		};
		
	     $scope.uploadFile = function(files) {
	         
	         var fd = new FormData();
	         //Take the first selected file
	         fd.append("file", files[0]);

	         homeService.upload(fd).then(
	             function(response){
	                 $scope.chosenFile= response.data;
	             },
	             function(response){
	                 alert("Greška pri upload-u slike.");
	             }
	         );
	     }
		
		function saveNewTheme(){
			if($scope.newObject.title.trim()==""){
				$scope.newObject.title="";
			}
			if($scope.newObject.text.trim()==""){
				$scope.newObject.text="";
			}
			if($scope.newObject.title!="" && $scope.newObject.text!=""){
				var theme={};
				theme.forum=$scope.forum;
				theme.title=$scope.newObject.title;
				theme.type=$scope.newObject.type;
				theme.author=$scope.user;
				theme.comments=[];
				theme.text=$scope.newObject.text;
				if(theme.type=="PICTURE"){
					theme.content=$scope.chosenFile;
				}else if(theme.type=="LINK"){
					theme.content=$scope.newObject.link;
				}else
					theme.content=null;
				theme.likes=0;
				theme.dislikes=0;
				theme.likeUsers=[];
				theme.dislikeUsers=[];
				homeService.saveTheme(theme).then(
						function(response){
							$scope.themes.push(response.data);
							alert('Uspešno ste kreirali temu.');
							$scope.createId=undefined;
							$scope.newObject=undefined;
							$scope.chosenFile=undefined;
							$scope.getTheme(response.data.id);
						},function(response){
							alert('Došlo je do greške prilikom kreiranja teme.');
						}
				);
			}
		};
		
		
		$scope.deleteTheme = function(){
			homeService.deleteTheme($scope.selectedTheme.id).then(
					function(response){
						alert('Tema je obrisana.');
						$scope.back("comment");
					}
					
			);
		};
		
		
		$scope.addManagerToList = function(user){
			if($scope.newObject.managers==undefined){
				$scope.newObject.managers=[];
			}
			user.added=true;
			$scope.newObject.managers.push(user);
		};
		
		$scope.removeManagerFromList = function(user){
			for(managerId in $scope.newObject.managers){
				if($scope.newObject.managers[managerId].username==user.username){
					$scope.newObject.managers[managerId].added=false;
					$scope.newObject.managers.splice(managerId,1);
				}
			}
		};
		
		$scope.createForum = function(){
			if($scope.newObject!=null && $scope.newObject.name!=null && $scope.newObject.description){
				if($scope.newObject.name.trim()==""){
					$scope.newObject.name="";
				}
				if($scope.newObject.description.trim()==""){
					$scope.newObject.description="";
				}
				if($scope.newObject.name.trim()!="" && $scope.newObject.description.trim()!=""){
					$scope.newObject.iconPath=$scope.chosenFile;
					$scope.newObject.manager=$scope.user;
					if($scope.newObject.managers==undefined)
						$scope.newObject.managers=[];
					$scope.newObject.managers.push($scope.user);
					saveForum($scope.newObject);
				}
			}
				
		};
		
		
		function saveForum(forum){
			var preparedForum={};
			preparedForum.name=forum.name;
			preparedForum.description=forum.description;
			preparedForum.iconPath=forum.iconPath;
			preparedForum.rules=forum.rules;
			preparedForum.manager=forum.manager;
			preparedForum.managers=forum.managers;
			homeService.saveForum(preparedForum).then(
					function(response){
						alert('Podforum je sačuvan.');
						init();
					},function(){
						alert("Došlo je do greške.");
					}
			);
		};
		
		$scope.deleteForum = function(forumName){
			homeService.deleteForum(forumName).then(
					function(response){
						alert('Forum je obrisan.');
						init();
						$scope.back('theme');
					},function(){
						alert('Došlo je do greške prilikom brisanja foruma.');
					}
			);
		};
		
		$scope.updateUser = function(user){
			homeService.updateUser(user).then(
					function(response){
						alert('Uspešno ste ažurirali podatke korisnika.');
						findAllUsers();
						$scope.createId=undefined;
						$scope.newObject=undefined;
						$scope.back('user');
					},function(){
						alert('Došlo je do greške prilikom ažuriranja korisnika.');
					}
			);
		}
		
		
		$scope.search = function(searchId){
			if(searchId=='forum'){
				searchForum(null,$scope.searchValue);
			}else if(searchId=='theme'){
				searchTheme(null,$scope.searchValue);
			}else if(searchId=='all'){
				$scope.allSearchResult={};
				$scope.allSearchResult.themes=[];
				$scope.allSearchResult.users=[];
				$scope.allSearchResult.forums=[];
				searchForum(searchId,$scope.searchValueAll);
				searchAllThemes(searchId,$scope.searchValueAll);
				searchUsers(searchId,$scope.searchValueAll);
				$scope.searchMode=true;
				if($scope.searchValueAll=='')
					init();
				
			}
		};
		
		function searchForum(searchId,searchValue){
			var searchResult=[];
			for(forumId in $scope.allForums){
				if(searchValue.indexOf(' ')==-1 && ($scope.allForums[forumId].name.toLowerCase().indexOf(searchValue.toLowerCase())!=-1 
						|| $scope.allForums[forumId].description.toLowerCase().indexOf(searchValue.toLowerCase())!=-1
						|| $scope.allForums[forumId].manager.username.toLowerCase().indexOf(searchValue.toLowerCase())!=-1)){
					searchResult.push($scope.allForums[forumId]);
					if(searchId=='all')
						$scope.allSearchResult.forums.push($scope.allForums[forumId]);
				}
				if(searchValue.indexOf(' ')!=-1){
					var valueList=searchValue.toLowerCase().split(' ');
					var found=true;
					for(value in valueList){
						if($scope.allForums[forumId].name.toLowerCase().indexOf(valueList[value])==-1 
								&& $scope.allForums[forumId].description.toLowerCase().indexOf(valueList[value])==-1
								&& $scope.allForums[forumId].manager.username.toLowerCase().indexOf(valueList[value])==-1){
							found=false;
						}
					}
					if(found==true){
						searchResult.push($scope.allForums[forumId]);
						if(searchId=='all')
							$scope.allSearchResult.forums.push($scope.allForums[forumId]);
					}
				}
			}
			$scope.forums=searchResult;
		};
		
		function searchTheme(searchId,searchValue){
			var searchResult=[];
			for(themeId in $scope.allThemes){
				if(searchValue.indexOf(' ')==-1 && ($scope.allThemes[themeId].title.toLowerCase().indexOf(searchValue.toLowerCase())!=-1
						|| $scope.allThemes[themeId].text.toLowerCase().indexOf(searchValue.toLowerCase())!=-1
						|| ($scope.allThemes[themeId].content!=null 
								&& $scope.allThemes[themeId].content.toLowerCase().indexOf(searchValue.toLowerCase())!=-1)
						|| $scope.allThemes[themeId].author.username.toLowerCase().indexOf(searchValue.toLowerCase())!=-1
						|| $scope.allThemes[themeId].forum.name.toLowerCase().indexOf(searchValue.toLowerCase())!=-1)){
					searchResult.push($scope.allThemes[themeId]);
				}
				if(searchValue.indexOf(' ')!=-1){
					var valueList=searchValue.toLowerCase().split(' ');
					var found=true;
					for(value in valueList){
						if($scope.allThemes[themeId].title.toLowerCase().indexOf(valueList[value])==-1
								&& $scope.allThemes[themeId].text.toLowerCase().indexOf(valueList[value])==-1
								&& $scope.allThemes[themeId].author.username.toLowerCase().indexOf(valueList[value])==-1
								&& $scope.allThemes[themeId].forum.name.toLowerCase().indexOf(valueList[value])==-1){
							found=false;
							break;
						}
					}
					if(found==true){
						searchResult.push($scope.allThemes[themeId]);
					}
				}
			}
			$scope.themes=searchResult;
		};
		
		function searchUsers(searchId,searchValue){
			homeService.findAllUsers().then(
					function(response){
						for(userId in response.data){
							if(searchValue.indexOf(' ')==-1 
									&& response.data[userId].username.toLowerCase().indexOf(searchValue.toLowerCase())!=-1){
								if(searchId=='all'){
									$scope.allSearchResult.users.push(response.data[userId]);
								}
							}
							if(searchValue.indexOf(' ')!=-1){
								var valueList=searchValue.toLowerCase().split(' ');
								var found=true;
								for(value in valueList){
									if(response.data[userId].username.toLowerCase().indexOf(valueList[value])==-1){
										found=false;
									}
								}
								if(found==true){
									if(searchId=='all'){
										$scope.allSearchResult.users.push(response.data[userId]);
									}
								}
							}
						}
					}
			);
		}
		
		function searchAllThemes(searchId,searchValue){
			homeService.findAllThemes().then(
					function(response){
						for(themeId in response.data){
							if(searchValue.indexOf(' ')==-1 
									&& (response.data[themeId].title.toLowerCase().indexOf(searchValue.toLowerCase())!=-1
									|| response.data[themeId].text.toLowerCase().indexOf(searchValue.toLowerCase())!=-1
									|| (response.data[themeId].content!=null 
											&& response.data[themeId].content.toLowerCase().indexOf(searchValue.toLowerCase())!=-1)
									|| response.data[themeId].author.username.toLowerCase().indexOf(searchValue.toLowerCase())!=-1
									|| response.data[themeId].forum.name.toLowerCase().indexOf(searchValue.toLowerCase())!=-1)){
								$scope.allSearchResult.themes.push(response.data[themeId]);
							}
							if(searchValue.indexOf(' ')!=-1){
								var valueList=searchValue.toLowerCase().split(' ');
								var found=true;
								for(value in valueList){
									if(response.data[themeId].title.toLowerCase().indexOf(valueList[value])==-1
											&& response.data[themeId].text.toLowerCase().indexOf(valueList[value])==-1
											&& response.data[themeId].author.username.toLowerCase().indexOf(valueList[value])==-1
											&& response.data[themeId].forum.name.toLowerCase().indexOf(valueList[value])==-1){
										found=false;
									}
								}
								if(found==true){
									$scope.allSearchResult.themes.push(response.data[themeId]);
								}
							}
						}
					}
			);
		}
		
		$scope.showHide = function(){
			document.getElementById("myDropdown").classList.toggle("show");
		}
		
		$scope.saveEntity = function(saveId, obj){
			if(saveId=='theme'){
				$scope.user.themes.push(obj);
				homeService.updateUser($scope.user).then(
						function(response){
							alert('Tema je sačuvana.');
							$window.localStorage.setItem("user", JSON.stringify(response.data));
							$scope.saveTheme=false;
						},function(){
							for(themeId in $scope.user.themes){
								if($scope.user.themes[themeId].id==obj.id){
									$scope.user.themes.splice(themeId,1);
									break;
								}
							}
							alert('Došlo je do greške prilikom čuvanja teme.');
						}
				);
			}else if(saveId=='comment'){
				$scope.user.comments.push(obj);
				homeService.updateUser($scope.user).then(
						function(response){
							alert('Komentar je sačuvan.')
							for(comId in $scope.comments){
								if($scope.comments[comId].id==obj.id){
									$scope.comments[comId].saveComment=false;
									break;
								}else{
									for(subComId in $scope.comments[comId].subComments){
										if($scope.comments[comId].subComments[subComId].id==obj.id){
											$scope.comments[comId].subComments[subComId].saveComment=false;
											break;
										}
									}
								}
							}
							$window.localStorage.setItem("user", JSON.stringify(response.data));
						},function(){
							for(comId in $scope.user.comments){
								if($scope.user.comments[comId].id==obj.id){
									$scope.user.comments.splice(comId,1);
									break;
								}
							}
							alert('Došlo je do greške prilikom čuvanja komentara.');
						}
				);
			}else if(saveId=='forum'){
				$scope.user.forums.push(obj);
				homeService.updateUser($scope.user).then(
						function(response){
							alert('Podforum je sačuvan.')
							$window.localStorage.setItem("user", JSON.stringify(response.data));
							$scope.saveForum=false;
						},function(){
							for(fId in $scope.user.forums){
								if($scope.user.forums[fId].name==obj.name){
									$scope.user.forums.splice(fId,1);
									break;
								}
							}
							alert('Došlo je do greške prilikom čuvanja podforuma.');
						}
				);
			}
		}
		
		$scope.deleteFollow = function(forum){
			for(fId in $scope.user.forums){
				if($scope.user.forums[fId].name==forum.name){
					$scope.user.forums.splice(fId,1);
					break;
				}
			}
			homeService.updateUser($scope.user).then(
					function(response){
						alert('Podforum više nije u listi praćenih podforuma.');
						$window.localStorage.setItem("user", JSON.stringify(response.data));
						$scope.saveForum=true;
						if($state.current.name=="user.followed"){
							$scope.back('theme');
						}
					},function(){
						$scope.user.forums.push(forum);
						alert('Došlo je do greške.');
					}
			);
		}
		
		
		
		
}]);