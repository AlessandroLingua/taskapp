(function () {
    var app = angular.module('TaskApp', []);

    app.controller('TaskCtrl', function($scope, $http) {
        $scope.tasks = [];
        $scope.categories = [];
        $scope.form = {};             // { title, description, categoryId }
        $scope.cat  = {};             // { name, description }
        $scope.filterCategoryId = ""; // filtro

        $scope.load = function() {
            $http.get('/api/categories').then(res => { $scope.categories = res.data; });
            $http.get('/api/tasks').then(res => { $scope.tasks = res.data; });
        };

        $scope.add = function() {
            if(!$scope.form.title){ $scope.err='Titolo obbligatorio'; return; }
            $http.post('/api/tasks', $scope.form).then(function(res){
                $scope.tasks.push(res.data);
                $scope.form = {}; $scope.ok='Creato!'; $scope.err=null;
            }, function(res){
                $scope.err = (res.data && (res.data.message || JSON.stringify(res.data.details))) || 'Errore creazione';
                $scope.ok = null;
            });
        };

        $scope.del = function(task) {
            if(!confirm('Eliminare il task #' + task.id + '?')) return;
            $http.delete('/api/tasks/' + task.id).then(function(){
                var i=$scope.tasks.indexOf(task); if(i>-1) $scope.tasks.splice(i,1);
            });
        };

        $scope.addCategory = function() {
            if(!$scope.cat.name) return;
            $http.post('/api/categories', $scope.cat).then(function(res){
                $scope.categories.push(res.data);
                $scope.cat = {};
            });
        };

        $scope.byCategory = function(t) {
            if(!$scope.filterCategoryId) return true;
            return t.categoryId === Number($scope.filterCategoryId);
        };
    });
})();