<?php 
/**
 * WordPress的admin_bar定制;
 */
add_action('wp_before_admin_bar_render','custom_admin_bar_menu');
function custom_admin_bar_menu(){
    global $wp_admin_bar;
    $wp_admin_bar->remove_menu('wp-logo');
        // wp-logo下的关于WordPress
        //$wp_admin_bar->remove_menu('about');
        // wp-logo下的官网链接
        //$wp_admin_bar->remove_menu('wporg');
        // wp-logo下的文档
        //$wp_admin_bar->remove_menu('documentation');
        // wp-logo下的支持论坛
        //$wp_admin_bar->remove_menu('support-forums');
        // wp-logo下的反馈
        //$wp_admin_bar->remove_menu('feedback');

    //$wp_admin_bar->remove_menu('site-name');//站点名称
    //$wp_admin_bar->remove_menu('view-site');//查看站点
 
    //$wp_admin_bar->remove_menu('updates');    //升级
    //$wp_admin_bar->remove_menu('appearance'); //外观
    //$wp_admin_bar->remove_menu('comments');   //评论
    //$wp_admin_bar->remove_menu('new-content');//新建

    $wp_admin_bar->add_menu(array(
        'id' => 'portal',
        'title' => 'Portal',
    ));

    $wp_admin_bar->add_menu(array(
        'parent' => 'portal',
        'id' => 'kahle',
        'title' => 'Kahle',
        'href' => 'https://github.com/kahle23/tool-store'
    ));

}
