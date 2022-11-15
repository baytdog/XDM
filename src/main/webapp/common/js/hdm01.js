// JavaScript Document
jQuery.easing['easeOutCubic'] = function (x, t, b, c, d) {
 return c*((t=t/d-1)*t*t + 1) + b;
};
function Tabs(tabs,list,block,item,$){
 var tabs  = $(tabs);
 var width = tabs.width();
 tabs.find(list+':first').addClass('active');
 tabs.find(item+':first').addClass('show');
 tabs.find(list).click(function(){
  var $this = $(this);
  var i = tabs.find(block).find('.show').index();
  var n = $this.index();
  $this.addClass('active').siblings('.active').removeClass('active');
  tabs.find(item).eq(n).addClass('show');
  if(n > i){
   tabs.find(block).stop().animate({'left':'-'+width+'px'},600,'easeOutCubic',function(){
    tabs.find(block).css('left','0px');
    tabs.find(item).eq(n).siblings('.show').removeClass('show');
   });
  }else{
   tabs.find(block).css('left','-'+width+'px').stop().animate({'left':0},600,'easeOutCubic',function(){
    tabs.find(item).eq(n).siblings('.show').removeClass('show');
   });
  }
 });
}