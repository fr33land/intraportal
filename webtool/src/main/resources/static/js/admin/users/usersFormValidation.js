$(document).ready(function () {
    $("#user-create-form").validate({
        onkeyup: false,
        errorElement: "span",
        errorClass: "is-invalid",
        rules: {
            username: {
                required: true,
                validusername: true,
                minlength: 3
            },
            firstname: {
                required: true

            },
            lastname: {
                required: true
            },
            email: {
                required: true,
                validemail: true,
                minlength: 5
            },
            passwordFirst: {
                required: true,
                minlength: 4
            },
            passwordSecond: {
                required: true,
                equalTo: "#passwordFirst"
            }
        },
        messages: {
            username: {
                minlength: "Your username must be at least 3 characters long"
            },
            passwordFirst: {
                minlength: "Your password must be at least 4 characters long"
            },
            passwordSecond: {
                minlength: "Your password must be at least 4 characters long",
                equalTo: "Please repeat the same password"
            }
        }
    });

    $("#user-edit-form").validate({
        onkeyup: false,
        errorElement: "span",
        errorClass: "is-invalid",
        rules: {
            username: {
                required: true,
                validusername: true,
                minlength: 3
            },
            firstname: {
                required: true

            },
            lastname: {
                required: true
            },
            email: {
                required: true,
                validemail: true,
                minlength: 5
            },
        },
        messages: {
            username: {
                minlength: "Your username must be at least 3 characters long"
            },
            passwordFirst: {
                minlength: "Your password must be at least 4 characters long"
            },
            passwordSecond: {
                minlength: "Your password must be at least 4 characters long",
                equalTo: "Please repeat the same password"
            }
        }
    });

    $("#user-password-form").validate({
        onkeyup: false,
        errorElement: "span",
        errorClass: "is-invalid",
        rules: {
            passwordFirst: {
                required: true,
                minlength: 4
            },
            passwordSecond: {
                required: true,
                equalTo: "#passwordFirst"
            }
        },
        messages: {
            passwordFirst: {
                minlength: "Your password must be at least 4 characters long"
            },
            passwordSecond: {
                minlength: "Your password must be at least 4 characters long",
                equalTo: "Please enter the same password as above"
            }
        }
    });
});