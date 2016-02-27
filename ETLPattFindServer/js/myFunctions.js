function scrollToBottom(i){
	window.scrollTo(0,document.body.scrollHeight);
	if(i > 0){
		setTimeout(function(){scrollToBottom(i-1)}, 10);
	}
}

jQuery(document).ready(function() {
    var offset = $(window).height()/2;
    var duration = 500;
    jQuery(window).scroll(function() {
        if (jQuery(this).scrollTop() > offset) {
            jQuery('.back-to-top').fadeIn(duration);
        } else {
            jQuery('.back-to-top').fadeOut(duration);
        }
    });
    
    jQuery('.back-to-top').click(function(event) {
        event.preventDefault();
        jQuery('html, body').animate({scrollTop: 0}, duration);
        return false;
    })
});