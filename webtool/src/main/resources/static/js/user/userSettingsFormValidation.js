$(document).ready(function () {
    $("#user-edit-form").validate({
        onkeyup: false,
        errorElement: "span",
        errorClass: "is-invalid",
        rules: {
            email: {
                required: true,
                validemail: true,
                minlength: 5
            },
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