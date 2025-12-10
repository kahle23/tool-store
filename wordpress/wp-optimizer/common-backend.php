<?php
/**
 * 对后台的一些改动;
 * Author: Kahle
 */


/**
 * 修改WordPress后台标题中的“ —— WordPress”;
 */
add_filter('admin_title', 'custom_admin_title', 10, 2);
function custom_admin_title($admin_title, $title) {
    return $title.' &lsaquo; '.get_bloginfo('name');
}

/**
 * 修改WP后台的左下方的版权信息和右下方的版本号;
 */
// 修改左下方的版权信息
add_filter('admin_footer_text', 'left_admin_footer_text', 11);
function left_admin_footer_text($text) {
    $text = 'Visit <a href="https://github.com/kahle23/tool-store" target="_blank">Kahle</a> Website.';
    return $text;
}
// 修改右下方的版本号
add_filter('update_footer', 'right_admin_footer_text', 11);
function right_admin_footer_text($text) {
    $text = '<a href="https://github.com/kahle23/tool-store" target="_blank">Kahle</a>';
    return $text;
}

/**
 * WordPress 后台仪表盘的定制;
 */
// 屏蔽 WordPress 后台“显示选项”选项卡
// function remove_screen_options() {
//     return false;
// }
// add_filter('screen_options_show_screen','remove_screen_options');
// 屏蔽 WordPress 后台“帮助”选项卡
function remove_screen_help($old_help, $screen_id, $screen) {
    $screen->remove_help_tabs();
    return $old_help;
}
add_filter('contextual_help','remove_screen_help',999,3);
// 移除 WordPress 后台仪表盘欢迎面板
remove_action('welcome_panel', 'wp_welcome_panel');
// 屏蔽 WordPress 后台仪表盘无用模块
function remove_dashboard_widgets() {  
    global $wp_meta_boxes;  
    // 删除 "快速发布" 模块  
    // unset($wp_meta_boxes['dashboard']['side']['core']['dashboard_quick_press']);
    // 删除 "近期草稿" 模块  
    unset($wp_meta_boxes['dashboard']['side']['core']['dashboard_recent_drafts']);  
    // 删除 "WordPress China 博客" 模块  
    //unset($wp_meta_boxes['dashboard']['side']['core']['dashboard_primary']);  
    // 删除 "其它 WordPress 新闻" 模块  
    //unset($wp_meta_boxes['dashboard']['side']['core']['dashboard_secondary']);  
    // 删除 "引入链接" 模块  
    unset($wp_meta_boxes['dashboard']['normal']['core']['dashboard_incoming_links']);  
    // 删除 "插件" 模块  
    unset($wp_meta_boxes['dashboard']['normal']['core']['dashboard_plugins']);  
    // 删除 "近期评论" 模块  
    unset($wp_meta_boxes['dashboard']['normal']['core']['dashboard_recent_comments']);  
    // 删除 "概况" 模块  
    unset($wp_meta_boxes['dashboard']['normal']['core']['dashboard_right_now']); 
    // 删除 "活动" 模块
    //unset($wp_meta_boxes['dashboard']['normal']['core']['dashboard_activity']);
}  
add_action('wp_dashboard_setup','remove_dashboard_widgets');

/**
 * 自定义要去除的顶级菜单;
 *  __('Dashboard')  仪表盘
 *  __('Posts')  文章
 *  __('Media')  媒体
 *  __('Links')  链接(WP3.0+默认已无)
 *  __('Pages')  页面
 *  __('Comments')  评论
 *  __('Appearance')  外观
 *  __('Plugins')  插件
 *  __('Users')  用户
 *  __('Tools')  工具
 *  __('Settings')  设置
 */
// function remove_menus() {
//   global $menu;
//   $restricted = array(__('Comments'),__('Users'));
//   end ($menu);
//   while (prev($menu)){
//     $value = explode(' ',$menu[key($menu)][0]);
//     if(strpos($value[0], '<') === FALSE) {
//       if(in_array($value[0] != NULL ? $value[0]:"" , $restricted)){
//         unset($menu[key($menu)]);
//       }
//     }
//     else {
//       $value2 = explode('<', $value[0]);
//       if(in_array($value2[0] != NULL ? $value2[0]:"" , $restricted)){
//         unset($menu[key($menu)]);
//       }
//     }
//   }
// }
// add_action('admin_menu', 'remove_menus');

/**
 * 删除不必要的子菜单;
 */
//function remove_submenu() {
    // 删除"仪表盘"下面的子菜单"更新"
//    remove_submenu_page('index.php','update-core.php');
    // 删除"外观"下面的子菜单"编辑"
//    remove_submenu_page('themes.php','theme-editor.php');
    // 删除"插件"下面的子菜单"编辑"
//    remove_submenu_page('plugins.php','plugin-editor.php');
    // 删除"工具"下面的子菜单"可用工具"
//    remove_submenu_page('tools.php','tools.php');
    // 删除"工具"下面的子菜单"导入"
//    remove_submenu_page('tools.php','import.php');
    // 删除"工具"下面的子菜单"导出"
//    remove_submenu_page('tools.php','export.php');
//}
//add_action('admin_init','remove_submenu');

/**
 * 删除外观中小工具内的项目;
 */
//function remove_wordpress_widgets() {
	// RSS
	// unregister_widget('WP_Widget_RSS');
	// 分类目录
    // unregister_widget('WP_Widget_Categories');
	// 功能
	// unregister_widget('WP_Widget_Meta');
	// 搜索
	// unregister_widget('WP_Widget_Search');
	// 文本
	// unregister_widget('WP_Widget_Text');
	// 文章归档
	// unregister_widget('WP_Widget_Archives');
	// 日历
	// unregister_widget('WP_Widget_Calendar');
	// 标签云
	// unregister_widget('WP_Widget_Tag_Cloud');
	// 自定义菜单
	// unregister_widget('WP_Nav_Menu_Widget');
	// 近期文章
	// unregister_widget('WP_Widget_Recent_Posts');
	// 近期评论
	// unregister_widget('WP_Widget_Recent_Comments');
	// 页面
	// unregister_widget('WP_Widget_Pages');
//}
//add_action('widgets_init','remove_wordpress_widgets');