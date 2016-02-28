<?php
	$newTaskDir = "data/newTaskInfo";
	$tasksDir = "data/tasks";
	$password = "klimek2016etl";

	session_start();
	include("html_codes.php");

	function alertMsg($type, $msg){
		?>
		<div class="alert alert-dismissable alert-<?php echo $type; ?>">
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			<strong><?php echo $msg; ?></strong>
		</div>
		<?php
	}

	function authorised($print = true){
		global $password;
		if (isset($_SESSION["authorised"]) && $_SESSION["authorised"] == sha1($password)) {
			return true;
		}
		else{
			if ($print) {
				alertMsg("danger", "You must be logged in!");
			}
			session_destroy();
			return false;
		}
	}
?>