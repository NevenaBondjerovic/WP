app.service('homeService', ['$http', function($http){
    
	var projectName="/wp";
	
	this.findAllForums = function(){
		return $http.get(projectName+"/forums");
	};

	this.findThemesByForum = function(forumName){
		return $http.get(projectName+"/themes?forumName="+forumName);
	};
	
	this.findCommentsByTheme = function(themeId){
		return $http.get(projectName+"/comments?themeId="+themeId);
	};
	
	this.findSubcommentsByComment = function(commentId){
		return $http.get(projectName+"/comments?commentId="+commentId);
	};
	
	this.findOneUser = function(username){
		return $http.get(projectName+"/users?username="+username);
	};
	
	this.updateTheme =function(theme){
		return $http.put(projectName+"/themes",theme);
	};
	
	this.updateComment=function(comment){
		return $http.put(projectName+"/comments",comment);
	};
	
	this.saveComment = function(comment){
		return $http.post(projectName+"/comments",comment);
	};
	
	this.deleteComment = function(commentId){
		return $http.delete(projectName+"/comments?commentId="+commentId);
	};
	
	this.saveTheme = function(theme){
		return $http.post(projectName+"/themes",theme);
	};
	
	this.deleteTheme = function(themeId){
		return $http.delete(projectName+"/themes?themeId="+themeId);
	};
	
	this.findAllUsers = function(){
		return $http.get(projectName+"/users");
	};
	
	this.saveForum = function(forum){
		return $http.post(projectName+"/forums",forum);
	};
	
	this.deleteForum = function(forumName){
		return $http.delete(projectName+"/forums?forumName="+forumName);
	};
	
	this.updateUser = function(user){
		return $http.put(projectName+"/users",user);
	};
	
	this.findAllThemes = function(){
		return $http.get(projectName+"/themes");
	};
	
	this.saveComplaint = function(complaint){
		return $http.post(projectName+"/complaints",complaint);
	};
	

	this.upload = function(fd){
	      return $http.post(projectName+"/themes/upload", fd, {
	        withCredentials: true,
	        headers: {'Content-Type': undefined },
	        transformRequest: angular.identity
	    });
	  }
		
	
}]);