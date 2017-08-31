var app = angular.module('app', ['ui.router']);


app.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise('/home');
    
    $stateProvider
    	.state('home', {
	    	url : '/home',
	      	templateUrl : 'home/home.html',
	        controller : 'homeController'
	    })
    	.state('login', {
	    	url : '/login',
	      	templateUrl : 'login/login.html',
	        controller : 'loginController'
	    })
	    .state('registration', {
	    	url : '/registration',
	      	templateUrl : 'registration/registration.html',
	        controller : 'registrationController'
	    })
	    .state('user', {
	    	url : '/user',
	      	templateUrl : 'user/user.html',
	        controller : 'userController'
	    })
	    .state('user.messages', {
	    	url : '/messages',
	      	templateUrl : 'user/messages.html',
	        controller : 'userController'
	    })
	    .state('user.followed', {
	    	url : '/followed',
	      	templateUrl : 'user/followed.html',
	        controller : 'userController'
	    })
	    .state('user.saved', {
	    	url : '/saved',
	      	templateUrl : 'user/saved.html',
	        controller : 'userController'
	    })
	    .state('user.appeals', {
	    	url : '/appeals',
	      	templateUrl : 'user/appeals.html',
	        controller : 'userController'
	    })
	    .state('user.profile', {
	    	url : '/profile',
	      	templateUrl : 'user/profile.html',
	        controller : 'userController'
	    });
}]);


app.directive('homeThemes', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/themes.html";

    return directive;
});


app.directive('newTheme', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/newTheme.html";

    return directive;
});

app.directive('homeComments', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/comments.html";

    return directive;
});

app.directive('homeProfile', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/profile.html";

    return directive;
});

app.directive('homeSubcomments', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/subComments.html";

    return directive;
});

app.directive('changeTheme', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/changeTheme.html";

    return directive;
});

app.directive('newForum', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/newForum.html";

    return directive;
});


app.directive('searchResult', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/searchResult.html";

    return directive;
});

app.directive('newComplaint', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "home/templates/newComplaint.html";

    return directive;
});

app.directive('userComplaint', function() {
	var directive = {};
    directive.restrict = 'E';
    directive.templateUrl = "user/templates/userComplaint.html";

    return directive;
});




app.directive('ngEnter', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if(event.which === 13) {
                scope.$apply(function(){
                    scope.$eval(attrs.ngEnter, {'event': event});
                });

                event.preventDefault();
            }
        });
    };
});



app.directive('fileModel', ['$parse', function ($parse) {
    return {
       restrict: 'A',
       link: function(scope, element, attrs) {
          var model = $parse(attrs.fileModel);
          var modelSetter = model.assign;
          
          element.bind('change', function(){
             scope.$apply(function(){
                modelSetter(scope, element[0].files[0]);
             });
          });
       }
    };
 }]);



app.directive('appFilereader', function($q) {

    var slice = Array.prototype.slice;

    return {
      restrict: 'A',
      require: '?ngModel',
      link: function(scope, element, attrs, ngModel) {
        if (!ngModel) return;

        ngModel.$render = function() {}

        element.bind('change', function(e) {
          var element = e.target;
          if(!element.value) return;

          element.disabled = true;
          $q.all(slice.call(element.files, 0).map(readFile))
            .then(function(values) {
              if (element.multiple) ngModel.$setViewValue(values);
              else ngModel.$setViewValue(values.length ? values[0] : null);
              element.value = null;
              element.disabled = false;
            });

          function readFile(file) {
            var deferred = $q.defer();

            var reader = new FileReader()
            reader.onload = function(e) {
              deferred.resolve(e.target.result);
            }
            reader.onerror = function(e) {
              deferred.reject(e);
            }
            reader.readAsDataURL(file);

            return deferred.promise;
          }

        }); //change

      } //link

    }; //return

  }) //appFilereader
;









