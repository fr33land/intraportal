const app = new Vue({
    el: '#content',
    data: {
        dataTable: null,
        apiError: undefined
    },
    computed: {},
    methods: {
        clearSearchInput: function (el) {
            el.parent().children('label').children('input[type=search]').val('');
            el.addClass("disabled");
            this.dataTable.search('').draw();
        },
        updateButtonState: function (el) {
            if (el.val().length > 0) {
                $(".clear-filter").removeClass("disabled");
            } else {
                $(".clear-filter").addClass("disabled");
            }
        }
    },
    beforeCreate() {
    },
    created() {
    },
    beforeMount() {
    },
    mounted() {
        let ctx = this;
        ctx.dataTable = $('#users-table').DataTable({
            'dom': '<"search-field-container row"<"col-2 h-35"f><"col-9 h-32"l><"add-user-button col-1">><t><"row justify-content-right"<"col-auto"p>><"clear">',
            'ajax': {
                'contentType': 'application/json',
                'url': '/web/admin/users/list',
                'type': 'POST',
                'data': function (d) {
                    return JSON.stringify(d);
                }
            },
            'columns': [
                {'data': 'id'},
                {'data': 'username'},
                {'data': 'email'},
                {'data': 'firstName'},
                {'data': 'lastName'},
                {'data': 'lastLogin'}
            ],
            'order': [[1, 'asc']],
            'columnDefs': [
                {
                    targets: 0,
                    visible: false,
                    orderable: false
                },
                {
                    targets: [1, 2],
                    createdCell: function (td, cellData, rowData, row, col) {
                        if(rowData.username === 'admin' && userName !== rowData.username) {
                            $(td).removeClass("edit-link");
                        } else {
                            $(td).addClass("edit-link");
                        }
                    }
                },
                {
                    targets: [3, 4],
                    render: function (data, type, rowData, meta) {
                        let node = ctx.dataTable.cell(meta.row, meta.col).nodes().to$();
                        if(rowData.username === 'admin' && userName !== rowData.username) {
                            node.removeClass("edit-link");
                        } else {
                            node.addClass("edit-link");
                        }
                        return data ? data : "";
                    }
                },
                {
                    targets: 5,
                    render: function (data, type, rowData, meta) {
                        let node = ctx.dataTable.cell(meta.row, meta.col).nodes().to$();
                        if(rowData.username === 'admin' && userName !== rowData.username) {
                            node.removeClass("edit-link");
                        } else {
                            node.addClass("edit-link");
                        }
                        return data ? moment(data).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                }
            ],
            'language': {
                'search': '_INPUT_',
                'searchPlaceholder': 'Filter'
            },
            'initComplete': function () {
                $('.paginate_button').addClass('btn btn-light');
            },
            'drawCallback': function () {
                $('.paginate_button').addClass('btn btn-light');
                $('#users-table tbody').on('click', '.edit-link', function () {
                    let username = ctx.dataTable.row(this).data().username;

                    if (username === 'admin' && userName !== username) {
                        document.location.href = '#';
                    } else if (userName === username) {
                        document.location.href = '/web/user/settings?ref=ul';
                    } else {
                        document.location.href = '/web/admin/users/edit/' + ctx.dataTable.row(this).data().id;
                    }
                });
            },
            'serverSide': true
        });

        $("div.add-user-button").html('<a href=/web/admin/users/new class = "btn btn-light" style="float:right">New user</a>');

        $("div.dataTables_filter").on('click', '.clear-filter', function () {
            ctx.clearSearchInput($(this));
        });

        $('input[type=search]').on('keyup', function () {
            ctx.updateButtonState($(this));
        });

    },
    beforeUpdate() {
    },
    updated() {
    }
});