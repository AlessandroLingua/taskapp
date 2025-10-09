(function () {
    var app = angular.module('TaskApp', []);

    app.controller('TaskCtrl', function($scope, $http) {
        $scope.tasks = [];
        $scope.paged = null;

        $scope.categories = [];
        $scope.form = {};             // { title, description, categoryId }
        $scope.cat  = {};             // { name, description }
        $scope.filterCategoryId = ""; // filtro

        // stato edit
        $scope.editMode = false;
        $scope.editId = null;

        // stato paging/search
        $scope.page = 0; $scope.size = 5; $scope.sort = 'title,asc';
        $scope.search = '';

        $scope.load = function() {
            $http.get('/api/categories').then(res => { $scope.categories = res.data; });
        };

        $scope.loadPaged = function() {
            const params = { page: $scope.page, size: $scope.size, sort: $scope.sort };
            if($scope.search) params.q = $scope.search;
            if($scope.filterCategoryId) params.categoryId = $scope.filterCategoryId;
            $http.get('/api/tasks/paged', { params }).then(res => {
                $scope.paged = res.data;
                $scope.tasks = res.data.content;
            });
        };

        $scope.startEdit = function(t) {
            $scope.editMode = true;
            $scope.editId = t.id;
            $scope.form = { title: t.title, description: t.description, categoryId: t.categoryId };
            $scope.ok = null; $scope.err = null;
        };

        $scope.cancelEdit = function() {
            $scope.editMode = false;
            $scope.editId = null;
            $scope.form = {};
        };

        $scope.save = function() {
            if(!$scope.form.title){ $scope.err='Titolo obbligatorio'; return; }
            if($scope.editMode) {
                $http.put('/api/tasks/' + $scope.editId, $scope.form).then(function(){
                    $scope.ok = 'Aggiornato!';
                    $scope.cancelEdit();
                    $scope.loadPaged();
                }, function(res){
                    $scope.err = (res.data && res.data.message) || 'Errore update';
                });
            } else {
                $http.post('/api/tasks', $scope.form).then(function(){
                    $scope.form = {}; $scope.ok='Creato!'; $scope.err=null;
                    $scope.loadPaged();
                }, function(res){
                    $scope.err = (res.data && (res.data.message || JSON.stringify(res.data.details))) || 'Errore creazione';
                    $scope.ok = null;
                });
            }
        };

        $scope.del = function(task) {
            if(!confirm('Eliminare il task #' + task.id + '?')) return;
            $http.delete('/api/tasks/' + task.id).then(function(){
                $scope.loadPaged();
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