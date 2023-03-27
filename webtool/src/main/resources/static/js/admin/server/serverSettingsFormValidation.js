$(document).ready(function () {
    $("#mail-settings-form").validate({
        onkeyup: false,
        errorElement: "span",
        errorClass: "is-invalid",
        rules: {
            host: {
                required: true,
                minlength: 2
            },
            port: {
                required: true,
                digits: true,
                min: 0,
                max: 65535
            },
            username: {
                required: true,
                minlength: 2
            },
            password: {
                required: true,
                minlength: 5
            },
        }
    });

});