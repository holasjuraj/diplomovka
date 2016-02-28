<?php
	include("functions.php");
	html_start("results");

	if (authorised()) {

	include("uploadAndLaunch.php");

	echo '<h4 class="heading">Submitted tasks</h4>';

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
			$infoFile = fopen("$dir/inputInfo.txt", "r");
			$taskName = fgets($infoFile);
			$startTime = fgets($infoFile);
			fgets($infoFile);	// Skip task directory
			$inputFile = fgets($infoFile);
			fclose($infoFile);

			$inputFilePure = substr($inputFile, 0, strrpos($inputFile, "."));
			$outputFile = "$dir/".$inputFilePure."_patterns.xml";
			$isReady = file_exists($outputFile);

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
			echo "</tr>\n";
		}
		?>
				</tbody>
			</table>
		</div>
		<?php
	}
	?>
	<a href="results.php"><button type="button" class="btn btn-success"><span class="glyphicon glyphicon-refresh"></span> Refresh</button></a>
	<div id="end"></div>
	<?php
	} // if (authorised())
	html_end("home");
?>