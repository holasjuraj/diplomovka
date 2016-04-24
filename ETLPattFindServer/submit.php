<?php
	include("functions.php");
	html_start("submit");

	if (authorised()) {
	
?>
	<h4 class="heading">Submit new task</h4>
	<form class="form-horizontal" method="post" action="results.php" enctype="multipart/form-data">
		<fieldset class="panel panel-info">
			<div class="panel-heading">General settings</div>
			<div class="panel-body">

				<div class="form-group">
					<label for="taskName" class="col-lg-2 col-xs-2 control-label">Name of the task:</label>
					<div class="col-lg-4 col-xs-6">
						<input type="text" class="form-control" id="taskName" name="taskName" value="Unnamed task">
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p style='text-align:left;'>
							Your own, human-readable name for the task.
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

				<div class="form-group">
					<label for="inputFile" class="col-lg-2 col-xs-2 control-label">Input file:</label>
					<div class="col-lg-4 col-xs-6">
						<input type="file" class="form-control" id="inputFile" name="inputFile">
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p class='text-left'>
							Upload input ETL file. You can use one of two forms:<br />
							<ul class='text-left'>
								<li>XML file</li>
								<li>zip archive, containing only one XML file. Only zip archive is allowed (i.e. no rar or tar.gz),
									and the archive cannot be locked by password.</li>
							</ul>
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

				<div class="form-group">
					<label for="min-pattern-size" class="col-lg-2 col-xs-2 control-label">Minimal pattern size:</label>
					<div class="col-lg-4 col-xs-6">
						<input type="text" class="form-control" id="min-pattern-size" name="min-pattern-size" value="2">
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p style='text-align:left;'>
							Show only patterns, that contain at least this number of ETL jobs. All patterns with less jobs will be omitted from the result.
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

				<div class="form-group">
					<label for="threads" class="col-lg-2 col-xs-2 control-label">Threads:</label>
					<div class="col-lg-4 col-xs-6">
						<select class="form-control" id="threads" name="threads">
							<option>1</option>
							<option>2</option>
							<option>3</option>
							<option selected>4</option>
						</select>
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p style='text-align:left;'>
							Number of threads used for this task. If you`re not running several tasks in parallel, just leave 4.
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

			</div>
		</fieldset>

		<fieldset class="panel panel-info">
			<div class="panel-heading">Job comparing settings</div>
			<div class="panel-body">

				<script type="text/javascript">
					function selectedCompMethod() {
						var val = document.getElementById("comparing-method").value;
						var formEdEst = document.getElementById("formEdEst");
						var formQgramSize = document.getElementById("formQgramSize");
						if (val == "editDistance") {
							formQgramSize.style.display = 'none';
							formEdEst.style.display = 'block';
						} else {
							formQgramSize.style.display = 'block';
							formEdEst.style.display = 'none';
						};
					}
				</script>
				<div class="form-group">
					<label for="comparing-method" class="col-lg-2 col-xs-2 control-label">Job comparing method:</label>
					<div class="col-lg-4 col-xs-6">
						<select class="form-control" id="comparing-method" name="comparing-method" onchange="selectedCompMethod()">
							<option value="editDistance">Edit distance</option>
							<option value="ukkonen" selected>Ukkonen distance</option>
							<option value="cosine">Cosine distance</option>
							<option value="jaccard">Jaccard index</option>
							<option value="sorensenDice">Sorensen-Dice coefficient</option>
						</select>
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p style='text-align:left;'>
							Method used for determining distance of two ETL jobs.<br />
							<ul class='text-left'>
								<li>
									<b>Edit distance:</b>
									theoretically most accurate, but quite slow - O(n^2). Computing time can be up to 2 hours.
								</li>
								<li>
									<b>Ukkonen distance:</b>
									based on L1 distance of vector-representation of the jobs. Fast - O(n).
								</li>
								<li>
									<b>Cosine distance:</b>
									based on cosine distance of vector-representation of the jobs. Fast - O(n).
								</li>
								<li>
									<b>Jaccard index:</b>
									based on Jaccard index
									of set-representation of the jobs. Fast - O(n).
								</li>
								<li>
									<b>Sorensen-Dice coefficient:</b>
									based on Sorensen-Dice coefficient
									of set-representation of the jobs. Fast - O(n).
								</li>
							</ul>
							The O(n) methods computing time is usually few minutes.
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

				<div class="form-group" id="formEdEst" style="display:none;">
					<label for="editdistance-early-stopping" class="col-lg-2 col-xs-2 control-label">Edit distance - early stopping threshold:</label>
					<div class="col-lg-4 col-xs-6">
						<input type="text" class="form-control" id="editdistance-early-stopping" name="editdistance-early-stopping" value="0.05">
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p style='text-align:left;'>
							If you use edit distance for comparing files, you can specify threshold for early stopping of the
							algorithm. The earlier is stops, the faster it is, but provides slightly less accurate result.
							Theoretically it should be greater or equal to clustering-threshold, but it works well even if
							this condition is not met.<br />
							Value must be at least 0.01 (1% of the algorithm will be executed), and at most 1.0 (whole algorithm will be executed).
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

				<div class="form-group" id="formQgramSize">
					<label for="qgram-size" class="col-lg-2 col-xs-2 control-label">Q-gram size:</label>
					<div class="col-lg-4 col-xs-6">
						<input type="text" class="form-control" id="qgram-size" name="qgram-size" value="2">
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p style='text-align:left;'>
							If you are using q-gram -based comparing of files (Ukkonen, cosine, Jaccard, Sorensen-Dice), you can
							specify the size of q-gram, i.e. how many consecutive XML tokens will be 'merged' into one logical unit.
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

			</div>
		</fieldset>

		<fieldset class="panel panel-info">
			<div class="panel-heading">Clustering settings</div>
			<div class="panel-body">

				<div class="form-group">
					<label for="clustering-method" class="col-lg-2 col-xs-2 control-label">Clustering method:</label>
					<div class="col-lg-4 col-xs-6">
						<select class="form-control" id="clustering-method" name="clustering-method">
							<option value="upgma" selected>UPGMA</option>
							<option value="clink">C-Link</option>
							<option value="slink">S-Link</option>
						</select>
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p style='text-align:left;'>
							Method used for determining distance of two clusters.<br />
							<ul class='text-left'>
								<li>
									<b>UPGMA (Unweighted Pair Group Method with Arithmetic Mean):</b>
									arithmetic average of distances of all pairs from cluster A and cluster B
								</li>
								<li>
									<b>C-Link (Complete Linkage):</b>
									maximal distance of two points from cluster A and cluster B
								</li>
								<li>
									<b>S-Link (Single Linkage):</b>
									minimal distance of two points from cluster A and cluster B
								</li>
							</ul>
							All methods are equally fast.
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

				<div class="form-group">
					<label for="clustering-threshold" class="col-lg-2 col-xs-2 control-label">Merging threshold:</label>
					<div class="col-lg-4 col-xs-6">
						<input type="text" class="form-control" id="clustering-threshold" name="clustering-threshold" value="0.05">
					</div>
					<a href="#" data-toggle="tooltip" class="tooltip-big" data-html="true" data-placement="right" title="
						<p style='text-align:left;'>
							Merging threshold for clustering - two clusters are merged if and only if distance between them is less or
							equal to this number (distance is always from range &lt;0.0, 1.0&gt;).<br />
							Roughly speaking, with lower value you get more clusters (patterns) with very similar jobs, with higher value you get less
							clusters with less similar jobs.<br />
							Value must be between 0.0001 and 1.0.
						</p>
						">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>

			</div>
		</fieldset>

		<div class="form-group">
			<div class="col-xs-12">
				<button type="submit" name="submit" class="btn btn-success btn-lg btn-block">Submit</button>
			</div>
		</div>


	</form>
<?php
	} // if (authorised())
	html_end("home");
?>