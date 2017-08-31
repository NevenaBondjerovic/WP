app.service('loginService', ['$http', function($http){

	var projectName="/wp";
	
	this.login = function(user){
		return $http.post(projectName+"/login", user);
	};

}]);