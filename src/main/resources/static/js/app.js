(function () {
    const app = angular.module('TaskApp', []);

    app.controller('TaskCtrl', function($scope, $http) {
        $scope.tasks = [];
        $scope.form = {};
        $scope.ok = null; $scope.err = null;

        $scope.load = function() {
            $http.get('/api/tasks').then(function(res){
                $scope.tasks = res.data;
            }, function() { $scope.err = 'Errore caricamento'; });
        };

        $scope.add = function() {
            if(!$scope.form.title) { $scope.err = 'Titolo obbligatorio'; return; }
            $http.post('/api/tasks', $scope.form).then(function(res){
                $scope.tasks.push(res.data);
                $scope.form = {}; $scope.ok = 'Creato!'; $scope.err = null;
            }, function(res) {
                $scope.err = (res.data && (res.data.message || JSON.stringify(res.data.details))) || 'Errore creazione';
                $scope.ok = null;
            });
        };

        $scope.del = function(task) {
            if(!confirm('Eliminare il task #' + task.id + '?')) return;
            $http.delete('/api/tasks/' + task.id).then(function(){
                var i = $scope.tasks.indexOf(task);
                if(i>-1) $scope.tasks.splice(i,1);
            });
        };
    });
})();