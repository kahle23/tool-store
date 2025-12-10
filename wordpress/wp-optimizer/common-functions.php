<?php
/**
 * 对功能的一些改动;
 * Author: Kahle
 */


/**
 * 解决Gravatar头像被墙，调用SSL头像链接;
 */
// function get_available_gravatar($avatar) {
//     $avatar = str_replace(array("www.gravatar.com","0.gravatar.com","1.gravatar.com","2.gravatar.com"),"secure.gravatar.com",$avatar);
//     //$avatar = preg_replace("/http:\/\/(www|\d).gravatar.com/","https://secure.gravatar.com",$avatar);
//     return $avatar;
// }
// add_filter('get_avatar','get_available_gravatar');

/**
 * 禁止后台显示更新;
 */
// 关闭核心提示
// add_filter('pre_site_transient_update_core',create_function('$a', "return null;"));
// 关闭插件提示
// add_filter('pre_site_transient_update_plugins',create_function('$a', "return null;"));
// 关闭主题提示
// add_filter('pre_site_transient_update_themes',create_function('$a', "return null;"));
// 禁止 WordPress 检查更新
// remove_action('admin_init', '_maybe_update_core');
// 禁止 WordPress 更新插件
// remove_action('admin_init', '_maybe_update_plugins');
// 禁止 WordPress 更新主题
// remove_action('admin_init', '_maybe_update_themes');

/**
* 禁用emoji表情;
*/
// 定义过滤函数用于删除tinymce编辑器的emoji表情插件
//function tinymce_emoji_disable($plugins) {
//    return array_diff($plugins, array('wpemoji'));
//}

// 禁用emoji表情的主要函数
function emoji_disable() {
    remove_action('wp_head', 'print_emoji_detection_script', 7);
    remove_action('admin_print_scripts', 'print_emoji_detection_script');
    remove_action('wp_print_styles', 'print_emoji_styles');
    remove_action('admin_print_styles', 'print_emoji_styles');
    remove_filter('the_content_feed', 'wp_staticize_emoji');
    remove_filter('comment_text_rss', 'wp_staticize_emoji');
    remove_filter('wp_mail', 'wp_staticize_emoji_for_email');
    add_filter('tiny_mce_plugins', 'tinymce_emoji_disable');
}
//add_action('init', 'emoji_disable');

/**
 * 禁止自动保存;
 */
function disable_auto_save(){
    wp_deregister_script('autosave');
}
add_action( 'wp_print_scripts', 'disable_auto_save', 10, 2 );

/**
 * 禁止版本控制;
 */
function disable_wp_revisions_to_keep( $num, $post ) {
    return 0;
}
add_filter( 'wp_revisions_to_keep', 'disable_wp_revisions_to_keep', 10, 2 );

/**
 * 保持ID连续性;
 */
function keep_id_continuous(){
    global $wpdb;
    // 删掉自动草稿和修订版本
    $wpdb->query("DELETE FROM `$wpdb->posts` WHERE `post_status` = 'auto-draft' OR `post_type` = 'revision'");
    // 自增值小于现有最大ID，MySQL会自动设置正确的自增值
    $wpdb->query("ALTER TABLE `$wpdb->posts` AUTO_INCREMENT = 1");  
}
add_filter( 'load-post-new.php', 'keep_id_continuous' );
// 由于此过滤器只能过滤部分上传操作，因此注释掉了本语句。
// 并在 处理上传的前置过滤器 中调用了 保持ID连续性 的方法。
//add_filter( 'load-media-new.php', 'keep_id_continuous' );
//add_filter( 'load-nav-menus.php', 'keep_id_continuous' );

/**
 * 处理上传的前置过滤器;
 */
//function handle_upload_prefilter($file){
    // 保持ID连续性
//    keep_id_continuous();
    // 获取路径信息
//    $info = pathinfo($file['name']);
    // 获取文件的后缀名信息
//    $ext = '.'.$info['extension'];
    // 修改上传文件的文件名
    // $file['name'] = date('YmdHis').uniqid().$ext;
//    $file['name'] = date('Ymd').uniqid().$ext;
//    return $file;
//}
//add_filter( 'wp_handle_upload_prefilter', 'handle_upload_prefilter' );

