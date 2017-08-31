app.service('userService', ['$http', function($http){
    
	var projectName="/wp";
    var url = projectName + "/users";

	this.findAll = function(){
		return $http.get(url);
	};
	
	this.findAllMessagesForUser = function(username){
		return $http.get(projectName+"/messages?username="+username);
	};
	

	this.updateMessage = function(message){
		return $http.put(projectName+"/messages",message);
	};
	
	this.saveMessage = function(message){
		return $http.post(projectName+"/messages",message);
	};
	
	this.findOneComplaint = function(complaintId){
		return $http.get(projectName+"/complaints?id="+complaintId);
	};
	
	this.findOneComment = function(commentId){
		return $http.get(projectName+"/comments?id="+commentId);	
	};
	
	this.findOneForum = function(name){
		return $http.get(projectName+"/forums?name="+name);
	};
	
	this.findOneTheme = function(id){
		return $http.get(projectName+"/themes?id="+id);
	};
	
	this.updateUser = function(user){
		return $http.put(projectName+"/users",user);
	};
	
	this.deleteComplaint = function(id){
		return $http.delete(projectName+'/complaints?id='+id);
	};
	
}]);