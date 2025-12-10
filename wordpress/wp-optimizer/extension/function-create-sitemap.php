<?php
/**
 * function to create sitemap.xml file in root directory of site;
 */
//add_action("save_post", "create_sitemap");
function create_sitemap() {
	if (str_replace('-', '', get_option('gmt_offset')) < 10) {
        $tempo = '-0' . str_replace('-', '', get_option('gmt_offset'));
    } else {
        $tempo = get_option('gmt_offset');
    }
    if (strlen($tempo) == 3) {
        $tempo = $tempo . ':00';
    }
    $postsForSitemap = get_posts(array(
        'numberposts' => - 1,
        'orderby' => 'modified',
        'post_type' => array('post','page'),
        'order' => 'DESC'
    ));
    $sitemap .= '<?xml version="1.0" encoding="UTF-8"?>';
    $sitemap .= "\n".'<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" '.
    "\n\t".'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" '.
    "\n\t".'xmlns:mobile="http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd" '.
    "\n\t".'xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 '.
    "\n\t\t".'http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">'."\n";
    $sitemap .= '<!-- generated-on=' . date( "Y-m-d\TH:i:s", current_time( 'timestamp', 0 ) ) . $tempo . '-->'."\n";
    $sitemap .= "\t".'<url>'."\n".
    "\t\t".'<loc>'. esc_url( home_url( '/' ) ) .'</loc>'.
    "\n\t\t".'<lastmod>' . date( "Y-m-d\TH:i:s", current_time( 'timestamp', 0 ) ) . $tempo . '</lastmod>'.
    "\n\t\t".'<changefreq>daily</changefreq>'.
    "\n\t\t".'<priority>1.0</priority>'.
    "\n\t".'</url>'."\n";
    foreach ($postsForSitemap as $post) {
        setup_postdata($post);
        $postdate = explode(" ", $post->post_modified);
        $sitemap .= "\t".'<url>'."\n".
        "\t\t".'<loc>'. get_permalink($post->ID) .'</loc>'.
        "\n\t\t".'<lastmod>'. $postdate[0] . 'T' . $postdate[1] . $tempo . '</lastmod>'.
        "\n\t\t".'<changefreq>Weekly</changefreq>'.
        "\n\t\t".'<priority>0.5</priority>'.
        "\n\t".'</url>'."\n";
    }
    $sitemap.= '</urlset>';
    $fp = fopen(ABSPATH . "sitemap.xml", 'w');
    fwrite($fp, $sitemap);
    fclose($fp);
}