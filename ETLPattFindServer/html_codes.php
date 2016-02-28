<?php

function html_start($page = false){
	global $db;
?>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9"> <![endif]-->
<!--[if !IE]><!--> <html lang="en"> <!--<![endif]-->
<head>
	<title>ETL Pattern Finder</title>
	<!-- Meta -->
	<meta charset="utf-8">
	<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=edge"><![endif]-->
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="author" content="Juraj Holas">
	<link href='http://fonts.googleapis.com/css?family=Lato:300,400,300italic,400italic' rel='stylesheet' type='text/css'>
	<link href='http://fonts.googleapis.com/css?family=Montserrat:400,700' rel='stylesheet' type='text/css'>
	
	<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="font-awesome/css/font-awesome.min.css" />
	<link rel="stylesheet" type="text/css" href="css/styles.css" />
		
	<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="js/respond.min.js"></script>
	<script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
	<script>
		$(document).ready(function(){
			$('[data-toggle="tooltip"]').tooltip();
		});

		jQuery(document).ready(function() {
			var offset = $(document).height() - $(window).height() - jQuery('.footer').height() - 50;
			var duration = 250;

			if (offset < 0) {
				jQuery('.scroll-down').hide();
			};

			jQuery(window).scroll(function() {
				if (jQuery(this).scrollTop() < offset) {
					jQuery('.scroll-down').fadeIn(0);
				} else {
					jQuery('.scroll-down').fadeOut(0);
				}
			});
			
			jQuery('.scroll-down').click(function(event) {
				event.preventDefault();
				jQuery('html, body').animate({scrollTop: $(document).height()}, duration);
				return false;
			})
		});
	</script>
	
</head>

<body class="no-js">
	<!-- ******HEADER****** -->
	<header>
	<div class="header container">
		<div class="toprow">
			<div class="navbar navbar-inverse">
			<nav>
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".top_nav">
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="index.php">ETL Pattern Finder</a>
				</div>

				<div class="navbar-collapse collapse navbar-inverse-collapse top_nav">
					<ul class="nav navbar-nav">
						<li<?php if($page == "submit"){ echo ' class="active"';} ?>><a href="submit.php">Submit new task</a></li>
						<li<?php if($page == "results"){ echo ' class="active"';} ?>><a href="results.php">Results</a></li>
					</ul>
				</div>
			</nav>
			</div>
		</div><!--//toprow-->
	</div><!--//container-->
	</header>
	
	<div class="container sections-wrapper">
		<div class="row">
			<main>
			
			<div class="primary col-lg-12 col-md-12 col-sm-12 col-xs-12">
				<div class="section">
<?php
}

function html_end($page = false){
	global $db;
?>
				</div><!--//section-->
			</div><!--//primary-->
			</main>

			<div class="main-delimiter col-sm-12 col-xs-12 hidden-lg hidden-md"></div>
			
		</div><!--//row-->
	</div><!--//container-->
	
	<!-- ******FOOTER****** -->
	<div class="footer">
	<footer>
		<div class="container">
			<div class="row">
				<div class="primary col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<div class="section text-center">
						&copy; Juraj Holas, 2016
						<div class="gray text-right" style="font-size:70%">
							Design: <a href="https://www.linkedin.com/in/xiaoying" target="_blank">Xiaoying Riley</a> z 
							<a href="http://themes.3rdwavemedia.com/" target="_blank">3rd Wave Media</a>
							<small><a href="http://creativecommons.org/licenses/by/3.0/" target="_blank">(License)</a></small><br />
						</div>
					</div>
				</div>
			</div>
		</div><!--//container-->
	</footer><!--//footer-->
	</div>
</body>
</html>
<?php
}

?>