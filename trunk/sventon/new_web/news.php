<div class="header">News</div>
<div class="body">
<?php
  $rss = fetch_rss('http://sventonblog.blogspot.com/atom.xml'); 
	echo "<ul>\r";
	$count = 0;
	foreach ($rss->items as $item) {
    $published = parse_w3cdtf($item['published']);
		$title = $item['title'];
		echo "<a name=\"$count\"></a>";
		echo "<li><b>";
    echo date("Y-m-d", $published);
    echo " - $title</b></li>\r";
    $content = $item['atom_content'];
    echo "$content\r<br/>\r";
    echo "<hr class=\"news\">";
    $count++;
	}
	echo "</ul>\r"; 
?> 
</div>