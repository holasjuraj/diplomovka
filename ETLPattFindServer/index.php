<?php
	include("functions.php");
	html_start("home");

	if (isset($_POST["logout"])) {
		$_SESSION["authorised"] = false;
	}

	if (isset($_POST["login"])) {
		if ($_POST["password"] == $password) {
			$_SESSION["authorised"] = sha1($_POST["password"]);
		} else {
			alertMsg("danger", "Incorrect password!");
		}
	}

	if (authorised(false)) {
		?>
		<h4 class="heading">Welcome</h4>
		<p>You can now proceed to <a href="submit.php">submit new task</a>, or to <a href="results.php">view results</a> of your previous tasks.</p>
		<br />
		<form method="post" action="index.php">
			<button type="submit" name="logout" type="button" class="btn btn-danger">Log out</button>
		</form>
		<?php
	} else {
?>
	<h4 class="heading">Log in</h4>
	<form class="form-horizontal" method="post" action="index.php">
		<fieldset>
			<div class="form-group">
				<label for="password" class="col-lg-2 col-xs-2 control-label">Password:</label>
				<div class="col-lg-4 col-xs-6">
					<input type="password" class="form-control" id="password" name="password">
				</div>
			</div>

			<div class="form-group">
				<div class="col-xs-12">
					<button type="submit" name="login" class="btn btn-success btn-lg">Log in</button>
				</div>
			</div>
		</fieldset>
	</form>
<?php
	}
	// finalize
	html_end("home");
?>