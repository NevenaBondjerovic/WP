app.controller('loginController',['$scope', '$state', '$window', 'loginService',
	function($scope, $state, $window, loginService){

		function init(){
			$scope.loginData = {};
		};

		init();

		$scope.login = function(){
			loginService.login($scope.loginData).then(
				function(response){
					$window.localStorage.setItem("user", JSON.stringify(response.data));
					$state.go('user.followed');
				},
				function(){
					alert('Uneli ste pogrešno korisničko ime ili lozinku.');
				}
			);
		};
		
}]);