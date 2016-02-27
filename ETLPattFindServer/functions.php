<?php
	include("html_codes.php");

	function dateToStr($timestamp){
		return date('j.n. Y', strtotime($timestamp));
	}
	
	function clr($string){
		if(isset($string)){
			return trim(addslashes(strip_tags($string)));
		}
		else{
			return "";
		}
	}

	function printPreview($articleRow = false){
		if(!$articleRow){ return false; }
		global $db;
		?>
		<article>
		<div class="article-preview section clearfix">
			<header class="header">
				<h2 class="heading"><a href="article.php?i=<?php echo($articleRow["id"]); ?>"><?php echo($articleRow["title"]); ?></a></h2>
				<ul class="metadata list-inline">
					<li><span class="badge">
							<span class="glyphicon glyphicon-calendar"></span><?php echo( dateToStr($articleRow["created"]) );
								if($articleRow["updated"] > $articleRow["created"]){
									echo( '&nbsp;&nbsp;&#124;&nbsp;&nbsp;<span class="glyphicon glyphicon-pencil"></span>'.dateToStr($articleRow["updated"]) );
								}
							?>
					</span></li>
					<li><span class="badge">
							<span class="glyphicon glyphicon-eye-open"></span><?php echo($articleRow["views"]); ?>
					</span></li>
					<li><span class="badge">
							<span class="glyphicon glyphicon-comment"></span><?php echo($articleRow["comments_num"]); ?>
					</span></li>
					<?php
						$query = "SELECT `tag` FROM `tags` WHERE `article_id` = ".$articleRow["id"]." GROUP BY `tag` ORDER BY `tag` ASC";
						$result = $db->query($query);
						foreach ($result as $row) {
							echo '<li><span class="badge">'.
								 '<span class="glyphicon glyphicon-tag"></span>'.$row["tag"].
								 "</span></li>\n";
						}
					?>
				</ul>
			</header>

			<div class="content">
				<p>
					<?php echo($articleRow["perex"]); ?>
				</p>							
			</div><!--//content-->
			<a href="article.php?i=<?php echo($articleRow["id"]); ?>" role="button" class="btn btn-cta-secondary btn-sm pull-right">
				Celý článok <span class="glyphicon glyphicon-chevron-right"></span>
			</a>
		</div><!--//section-->
		</article>
		<?php
		return true;
	}

	function printComment($commentRow = false){
		if(!$commentRow){ return false; }
		?>
		<article>
		<div class="comment panel panel-default">
			<div class="panel-heading">
			<header>
				<h5 class="text-primary"><?php echo($commentRow["title"]); ?></h5>
				<ul class="metadata list-inline">
					<li><span class="badge">
						<span class="glyphicon glyphicon-calendar"></span><?php echo( dateToStr($commentRow["created"]) ); ?>
					</span></li>
					<li><span class="badge">
						<span class="glyphicon glyphicon-user"></span><?php echo($commentRow["author"]); ?>
					</span></li>
				</ul>
			</header>
			</div>
			
			<div class="panel-body">
				<?php echo($commentRow["content"]); ?>
			</div>
		</div>
		</article>
		<?php
		return true;
	}
?>