app.service('registrationService', ['$http', function($http){
    
	var projectName="/wp";
    var url = projectName+"/registration";

	this.registration = function(user){
		return $http.post(url, user);
	};

}]);