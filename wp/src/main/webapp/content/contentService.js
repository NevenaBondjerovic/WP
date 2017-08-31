app.service('contentService', ['$http', function($http){
    
	var projectName="";
    var url = projectName+"/users";

	this.findAll = function(){
		return $http.get(url);
	};

}]);