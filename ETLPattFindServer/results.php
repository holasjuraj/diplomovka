<?php
	include("functions.php");
	html_start("results");

	if (authorised()) {

	include("uploadAndLaunch.php");

	if (isset($_POST["star"])) {
		foreach ($_POST["star"] as $taskID => $starArr) {
			foreach ($starArr as $star => $temp) {
				$ratingFile = fopen("$tasksDir/$taskID/rating.txt", "w");
				fwrite($ratingFile, "$star\n");
				fclose($ratingFile);
			}
		}
	}

	?>
	<a href="#" class="scroll-down"><span class="glyphicon glyphicon-chevron-down"></span></a>
	<h4 class="heading">Submitted tasks</h4>
	<?php

	$tasksDirList = glob($tasksDir."/*");
	$countTasks = count($tasksDirList);
	if ($countTasks < 0) {
		echo "No submitted tasks yet.";
	} else {
		?>
		<div class="table-responsive">
			<table class="table table-bordered table-striped table-hover">
				<thead>
					<tr>
						<th>Task name</th>
						<th>Submit time</th>
						<th>Input file</th>
						<th>Status</th>
						<th>Ready</th>
						<th>Download result</th>
						<th>Rate result</th>
					</tr>
				</thead>
				<tbody>
		<?php
		for ($i = 0; $i < $countTasks; $i++) {
			$dir = $tasksDirList[$i];
			if (!is_dir($dir)) {
				continue;
			}
			if (!file_exists("$dir/inputInfo.txt")) {
				continue;
			}
			$taskID = substr($dir, strrpos($dir, "/") + 1);
			$infoFile = fopen("$dir/inputInfo.txt", "r");
			$taskName = fgets($infoFile);
			$startTime = fgets($infoFile);
			fgets($infoFile);	// Skip task directory
			$inputFile = fgets($infoFile);
			fclose($infoFile);

			$inputFilePure = substr($inputFile, 0, strrpos($inputFile, "."));
			$outputFile = "$dir/".$inputFilePure."_patterns.xml";
			$isReady = file_exists($outputFile);

			$rating = 0;
			if (file_exists("$dir/rating.txt")) {
				$ratingFile = fopen("$dir/rating.txt", "r");
				$rating = 0 + fgets($ratingFile);
				fclose($ratingFile);
			}

			echo "<tr>\n";
			echo "<td>$taskName</td>\n";
			echo "<td>$startTime</td>\n";
			echo "<td>$inputFile</td>\n";
			echo '<td class="text-center"><a href="status.php?task='.substr($dir, strrpos($dir, "/")+1)
				.'#end" target="_blank">view status <span class="glyphicon glyphicon-new-window"></span></a></td>'."\n";
			echo '<td class="text-center">';
			if ($isReady) {
				echo "<span class=\"glyphicon glyphicon-ok\"></span></td>\n";
				echo '<td class="text-center" style="padding:0;"><a href="'.$outputFile.'" download><button type="button" class="btn btn-primary btn-xxs">'
					.'<span class="glyphicon glyphicon-download-alt"></span></button></a></td>'."\n";
			} else {
				echo "<span class=\"glyphicon glyphicon-remove\"></span></td>\n";
				echo "<td></td>\n";
			}
			echo '<td class="text-center"><form action="" method="post">';
			for ($r = 1; $r <= 5; $r++) {
				echo '<button class="star" type="submit" name="'."star[$taskID][$r]".'">'
					.'<a href="#" data-toggle="tooltip" title="'.$r.'">'
					.'<span class="glyphicon glyphicon-star'.(($rating >= $r) ? "" : "-empty").'">'
					.'</span></a></button>&nbsp;';
			}
			echo "</form></td>\n";
			echo "</tr>\n";
		}
		?>
				</tbody>
			</table>
		</div>
		<?php
	}
	?>
	<a href="">
		<button type="button" class="btn btn-success"><span class="glyphicon glyphicon-refresh"></span> Refresh</button>
	</a>
	<div id="end"></div>
	<?php
	} // if (authorised())
	html_end("home");
?>