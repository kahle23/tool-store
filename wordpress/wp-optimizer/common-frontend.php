<?php
/**
 * 对前端的一些改动;
 * Author: Kahle
 */


/**
 * WordPress 前台的head优化;
 */
// 移除文章feed;
//remove_action('wp_head','feed_links',2); 
// 移除评论feed;
//remove_action('wp_head','feed_links_extra',3); 
// 移除针对Blog的远程离线编辑器接口;
//remove_action('wp_head','rsd_link');
// 移除Windows Live Writer接口;
//remove_action('wp_head','wlwmanifest_link');
// 移除<meta name="generator" content="WordPress 版本号" />;
//remove_action('wp_head','wp_generator');
// 移除自动生成的短链接;
//remove_action('wp_head','wp_shortlink_wp_head', 10, 0 );

/**
 * 修改前端代码中的 WordPress 版本号;
 */
//function remove_wp_version_strings( $src ) {
//  global $wp_version;
//  parse_str(parse_url($src, PHP_URL_QUERY), $query);
//  if ( !empty($query['ver']) && $query['ver'] === $wp_version ){
//    $src = str_replace($wp_version, 'hello_world', $src);
//  }
//  return $src;
//}
//add_filter( 'script_loader_src', 'remove_wp_version_strings');
//add_filter( 'style_loader_src', 'remove_wp_version_strings');

/**
 * 增加站点底部的前端代码;
 */
function website_diy_add_foot_code() {
	echo "
    <script type=\"text/javascript\">
        jQuery(\"div[class='site-info']\").html(\"<a href='".get_bloginfo('siteurl')."' title='".get_bloginfo('description')."'>".get_bloginfo('name')." 2015 - ".date('Y',time())."</a>\");
    </script>
    ";
}
add_action('wp_footer','website_diy_add_foot_code');
