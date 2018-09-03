    $('.menu .item').tab();

    $('.message .close')
        .on('click', function() {
            $(this)
                .closest('.message')
                .transition('fade')
            ;
        });
    $("#loginDiv").fadeIn(1500);