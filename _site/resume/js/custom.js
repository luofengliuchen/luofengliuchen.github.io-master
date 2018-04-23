// templatemo 467 easy profile

// PRELOADER

$(window).load(function(){
    $('.preloader').delay(1000).fadeOut("slow"); // set duration in brackets    
});

// HOME BACKGROUND SLIDESHOW
$(function(){
    jQuery(document).ready(function() {
		$('body').backstretch([
	 		 "http://7xpui7.com1.z0.glb.clouddn.com/bg-resume.jpg", 
	 		 "http://7xpui7.com1.z0.glb.clouddn.com/bg-resume5.jpg",
			 "http://7xpui7.com1.z0.glb.clouddn.com/bg-resume6.jpg"
	 			], 	{duration: 3200, fade: 1300});
		});
})