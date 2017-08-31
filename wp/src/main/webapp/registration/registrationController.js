app.controller('registrationController',['$scope', '$window', '$state', 'registrationService',
	function($scope, $window, $state, registrationService){

		function init(){
			$scope.user = {};
			$scope.repeatedPass = "";
		};

		init();

		$scope.registration = function(){
			if($scope.repeatedPass!=$scope.user.password)
				alert("Pogrešna lozinka.");
			else{
				registrationService.registration($scope.user).then(
						function(response){
							$window.localStorage.setItem("user", JSON.stringify(response.data));
							$state.go('user.followed');
							alert('Uspešno ste se registrovali. Dobrodošli!');
						},
						function(){
							alert('Korisničko ime koje ste uneli već postoji.');
						}
				);
			}
		}
		
}]);