/*******************************************************************************
 * jQuery.mb.components: jquery.mb.CSSAnimate
 * version: 1.0- 04/12/11 - 18
 * © 2001 - 2011 Matteo Bicocchi (pupunzi), Open Lab
 *
 * Licences: MIT, GPL
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 *
 * email: mbicocchi@open-lab.com
 * site: http://pupunzi.com
 *
 *  params:

 @opt        -> the CSS object (ex: {top:300, left:400, ...})
 @duration   -> an int for the animation duration in milliseconds
 @ease       -> ease  ||  linear || ease-in || ease-out || ease-in-out  ||  cubic-bezier(<number>, <number>,  <number>,  <number>)
 @properties -> properties to which CSS3 transition should be applied.
 @callback   -> a callback function called once the transition end

 example:

 $(this).CSSAnimate({top: t, left:l, width:w, height:h}, 2000, "ease-out", "all", function() {el.anim();})
 ******************************************************************************/

$.fn.CSSAnimate=function(a,c,h,i,j,e){return this.each(function(){var d=$(this);if(0!==d.length&&a){"function"==typeof c&&(e=c);"function"==typeof h&&(e=h);"function"==typeof i&&(e=i);"function"==typeof j&&(e=j);if("string"==typeof c)for(var k in $.fx.speeds)if(c==k){c=$.fx.speeds[k];break}else c=null;c||(c=$.fx.speeds._default);i||(i="cubic-bezier(0.65,0.03,0.36,0.72)");j||(j="all");h||(h=0);if(jQuery.support.transition){var b="",f="transitionEnd";$.browser.webkit?(b="-webkit-",f="webkitTransitionEnd"): $.browser.mozilla?(b="-moz-",f="transitionend"):$.browser.opera?(b="-o-",f="oTransitionEnd"):$.browser.msie&&(b="-ms-",f="msTransitionEnd");for(var g in a)"transform"===g&&(a[b+"transform"]=a[g],delete a[g]),"transform-origin"===g&&(a[b+"transform-origin"]=a[g],delete a[g]);d.css(b+"transition-property",j);d.css(b+"transition-duration",c+"ms");d.css(b+"transition-delay",h+"ms");d.css(b+"transition-timing-function",i);setTimeout(function(){d.css(a)},1);var l=function(){d.get(0).removeEventListener(f, l,!1);d.css(b+"transition","");"function"==typeof e&&e()};d.get(0).addEventListener(f,l,!1)}else d.animate(a,c,e)}})};$.fn.CSSAnimateStop=function(){var a="";$.browser.webkit?a="-webkit-":$.browser.mozilla?a="-moz-":$.browser.opera?a="-o-":$.browser.msie&&(a="-ms-");$(this).css(a+"transition","")}; $.support.transition=function(){var a=(document.body||document.documentElement).style;return void 0!==a.transition||void 0!==a.WebkitTransition||void 0!==a.MozTransition||void 0!==a.MsTransition||void 0!==a.OTransition}();

/*******************************************************************************
 * jQuery.mb.components: jquery.mb.disclose
 * version: 1.0
 * © 2001 - 2012 Matteo Bicocchi (pupunzi), Open Lab
 *
 * Licences: MIT, GPL
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 *
 * email: mbicocchi@open-lab.com
 * site: http://pupunzi.com
 *
 DATA:

 CONTAINERS:

 data-animationin
 data-animationout
 data-time
 data-onenter
 data-onexit
 data-stop

 ELEMENTS:

 data-animate
 data-animationstart
 data-animationend
 data-animationtime
 data-animationdelay
 data-ease

 ******************************************************************************/

(function($){

    $.disclose = {
        name:"mb.disclose",
        author:"Matteo Bicocchi",
        version:"1.0",
        defaults:{
            slideInterval:5000,
            inTimer:600,
            outTimer:1200,
            ease:"bezier(.24,.85,.32,.92)",
            defaultIn:{left:"100%", top:0, opacity:1},
            defaultOut:{left:"-100%", top:0, opacity:1},
            autoPlay:false,
            autoRestart:false,
            stopOnHover:true,
            activateKeyboard:false,
            indexPlaceHolder:"#slideIndex",
            progressPlaceHolder:"#slideProgress",
            onEnter:function(el){},
            onExit:function(el){},
            onInit:function(el){}
        },

        init: function(opt){
            var arg = arguments;
            return this.each(function(){
                var el= this;
                var $el= $(this);
                var pos = $el.css("position") == "static" ? "relative" : $el.css("position");
                $el.css({overflow:"hidden", position: pos});

                if(typeof arg[0] == "string"){
                    switch(arg[0]){
                        case "goto":
                            $.disclose.goTo(el, arg[1]);
                            break;
                        case "next":
                            $.disclose.next(el, arg[1]);
                            break;
                        case "prev":
                            $.disclose.prev(el, arg[1]);
                            break;
                        case "play":
                            $.disclose.play(el);
                            break;
                        case "pause":
                            $.disclose.pause(el);
                            break;
                        case "restore":
                            $.disclose.restore(el);
                            break;
                        case "refresh":
                            $.disclose.refresh(el);
                            break;
                    }
                    return;
                }
                el.page=0;
                el.hasTouch = 'ontouchstart' in window;
                el.isIE = $.browser.msie;

                el.opt = {};
                el.opt.id = el.id ? el.id : "id_"+ new Date().getTime();
                $.extend (el.opt, $.disclose.defaults,opt);

                el.container = $("<div/>").attr("id","mbDiscloseCont_"+el.opt.id);
                el.container.css({position:"absolute",top:-5000,left:-5000});
                $("body").append(el.container);

                var pages = $(el).children();

                pages.css({height: $(el).height(), boxSizing:"border-box", overflow:"hidden"});
                var bannerWrapper = $("<div/>").addClass("mbDiscloseWrapper");
                pages.show();
                el.container.append(pages);
                pages.wrap(bannerWrapper);

                el.pages= el.container.children();

                el.pages.each(function(){
                    $(this).data("idx",$(this).index());
                });
                if(el.opt.activateKeyboard)
                    $(document).bind("keydown", function(e){
                        var key= e.which;

                        switch(key){
                            case 37:
                                $.disclose.prev(el,true);
                                e.preventDefault();
                                break;
                            case 39:
                                $.disclose.next(el,true);
                                e.preventDefault();
                                break;
                            case 32:
                                $.disclose.play(el);
                                e.preventDefault();
                                break;
                        }
                    });

                $(window).bind("resize",function(){
                    $.disclose.refresh(el);
                });
                $.disclose.start(el);

                if(typeof el.opt.onInit == "function")
                    el.opt.onInit(el);
            })
        },

        start:function(el){
            var banner= el.pages.eq(el.page).clone(true);
            banner.hide().css({top:0,left:0, opacity:1});
            $(el).append(banner);
            banner.show();

            var bannerProp = banner.children().eq(0).data();
            var fn= bannerProp.onenter ? eval("("+bannerProp.onenter+")") : el.opt.onEnter;

            if(typeof fn == "function")
                fn(el);

            $(el).css("visibility","visible");
            el.actualBanner = banner;
            el.actualBanner.addClass("inPage");

            if($(el.opt.indexPlaceHolder).length>0){
                $.disclose.buildIndex(el);
            }

            $.disclose.animateElements(el);

            if(el.pages.length<=1)
                return;

            if(el.opt.autoPlay){

                var dataTime = bannerProp.time;
                var $newElTime = dataTime ? dataTime : el.opt.slideInterval;

                el.interval = setTimeout(function(){$(el).disclose("next")},$newElTime);

            }

            if(el.opt.stopOnHover && el.opt.autoPlay)
                $(el).bind("mouseenter",function(){
                    el.opt.autoPlay=false;
                    clearTimeout(el.interval);
                }).bind("mouseleave",function(){
                        el.opt.autoPlay=true;
                        $.disclose.showProgress(el);
                        el.interval = setTimeout(function(){$(el).disclose("next")},el.opt.slideInterval);
                    });

            if(el.hasTouch){
                $(el).swipe({
                    swipeLeft:function(el){
                        $.disclose.next(el);
                    },
                    swipeRight:function(el){
                        $.disclose.prev(el);
                    }
                });

                $(el).doubleTap({
                    func:function(el){
                        $.disclose.play(el);
                    }
                })
            }
        },

        next:function(el, stopSlide){

            if(stopSlide){
                clearTimeout(el.interval);
                el.opt.autoPlay=false;
            }

            if(el.page < el.pages.length-1)
                el.page++;
            else
                el.page=0;

            $.disclose.goTo(el);
        },

        prev:function(el, stopSlide){

            if(stopSlide){
                clearTimeout(el.interval);
                el.opt.autoPlay=false;
            }

            if(el.page > 0)
                el.page--;
            else
                el.page=el.pages.length-1;

            $.disclose.goTo(el);
        },

        goTo:function(el, idx, stop){

            if(el.pages.length<=1)
                return;

            clearTimeout(el.interval);

            if(idx>=0 && idx == el.page)
                return;

            el.page = idx>=0 ? idx : el.page;
            el.page = el.page > el.pages.length-1 ? 0 : el.page;


            var $oldEl = $(el).children().eq(0).css({zIndex:0});
            var $newEl = el.pages.eq(el.page).clone(true).css({zIndex:1});

            var $oldElProp = $oldEl.children().eq(0).data();
            var $newElProp = $newEl.children().eq(0).data();

            var dataAnimOut = $oldEl.getAnimation($oldElProp.animationout, el);
            var $oldElAnim = dataAnimOut ? dataAnimOut : el.opt.defaultOut;

            var dataAnimIn = $newEl.getAnimation($newElProp.animationin, el);
            var $newElAnim = dataAnimIn ? dataAnimIn : el.opt.defaultIn;

            $newElAnim = $.normalizeTransform($newElAnim);

            var dataTime = $newElProp.time;
            var $newElTime = dataTime ? dataTime : el.opt.slideInterval;

            function removeEls(element){
                element.CSSAnimate($oldElAnim, el.opt.outTimer, null, el.opt.ease, "all", function(){
                    element.remove();
                    //console.debug(element)
                });
            }

            for (var i=0; i< $(el).children().length; i++){
                if (i>0)
                    removeEls($(el).children().eq(i));
            }

            if($newElProp.stop || stop){
                el.opt.autoPlay=false;

                if(el.opt.autoRestart>0)
                    setTimeout(function(){el.opt.autoPlay=true},el.opt.autoRestart);
            }

            $(el).append($newEl);
            $newEl.css($newElAnim);

            /*ENTER*/
            el.actualBanner = $newEl;
            el.actualBanner.addClass("inPage");
            setTimeout(function(){
                el.actualBanner.CSSAnimate({top:0, left:0, opacity:1, transform: "rotate(0deg) scale(1)" }, el.opt.inTimer,null, el.opt.ease, "all", function(){
                    var fn= $newElProp.onenter ? eval("("+$newElProp.onenter+")") : el.opt.onEnter;
                    if(typeof fn == "function")
                        fn(el);
                });

                $.disclose.animateElements(el);
                if(el.opt.autoPlay){
                    el.interval = setTimeout(function(){$(el).disclose("next")},$newElTime);
                }

                if($(el.opt.indexPlaceHolder).length>0){
                    $.disclose.buildIndex(el);
                }
            },100);

            /*Exit*/
            var fn= $oldElProp.onexit ? eval("("+$oldElProp.onexit +")") : el.opt.onExit;
            if(typeof fn == "function")
                fn(el);

            setTimeout(function(){
                $oldEl.CSSAnimate($oldElAnim, el.opt.outTimer, null, el.opt.ease, "all", function(){
                    $oldEl.remove();
                });
            },100);
        },

        play:function(el){
            clearTimeout(el.interval);
            el.opt.autoPlay = !el.opt.autoPlay;

            if(el.opt.autoPlay){
                $.disclose.next(el);
            }
        },

        pause:function(el){
            clearTimeout(el.interval);
            el.opt.autoPlay=false;
        },

        restore:function(el){
            el.opt.autoPlay=true;
            el.interval = setTimeout(function(){$(el).disclose("next")},el.opt.slideInterval);
        },

        animateElements:function(el){

            var $el = el.actualBanner;
            var $els = $el.find("[data-animate=true]");

            $els.each(function(){
                var $el = $(this);

                var $elProp = $el.data();
                var cssStart = $elProp.animationstart ? $elProp.animationstart : {opacity:0};
                cssStart = $.normalizeTransform(cssStart);

                var cssEnd = $elProp.animationend ? $elProp.animationend : {opacity:1};
                cssEnd = $.normalizeTransform(cssEnd);

                var time = $elProp.animationtime ? $elProp.animationtime : el.opt.inTimer;
                var cssDelay = $elProp.animationdelay ? $elProp.animationdelay : 700;
                var ease = $elProp.ease ? $elProp.ease : el.opt.ease;

                $el.css(cssStart);
                setTimeout(function(){
                    $el.CSSAnimate(cssEnd, time, cssDelay, ease, "all", function(){});
                },100);
            });
        },

        /* ANIMATIONS:
         NAME : [CSS BEFORE ENTERING, CSS AFTER EXITING]
         */
        animations:{
            fade: [{opacity:0, left:0, top:0, right:"auto", bottom:"auto"},{opacity:0, left:0, top:0, right:"auto", bottom:"auto"}],
            slideLeft: [{left:"-100%", top:0, right:"auto", bottom:"auto", opacity:1},{left:"100%", top:0, right:"auto", bottom:"auto", opacity:1}],
            slideRight:[{left:"100%", opacity:1, right:"auto", bottom:"auto"},{left:"-100%", opacity:1, right:"auto", bottom:"auto"}],
            slideUp: [{top:"-100%", opacity:1, left:0, right:"auto", bottom:"auto"},{top:"100%", opacity:1, left:0, right:"auto", bottom:"auto"}],
            slideDown: [{top:"100%", opacity:1, left:0, right:"auto", bottom:"auto"},{top:"-100%", opacity:1, left:0, right:"auto", bottom:"auto"}],
            scaleIn: [{opacity:0, transform:"scale(10)"},{opacity:0, transform:"scale(.1)"}],
            scaleOut: [{opacity:1, transform:"scale(.1)"},{opacity:1, transform:"scale(10)"}],
            defaultAnim:[{ opacity:0}, {opacity:0}]
        },

        getAnimation:function(anim){
            if(typeof anim === "object")
                return anim;

            if(!anim || anim == undefined || !$.disclose.animations[anim])
                anim = "defaultAnim";

            var $el= this;
            var isInPage = $el.hasClass("inPage") || $el.parents(".inPage").length>1;
            return isInPage ? $.disclose.animations[anim][0] : $.disclose.animations[anim][1];
        },

        setAnimation:function(anim){
            $.extend($.disclose.animations, anim);
        },

        buildIndex:function(el){

            var indexBox = $(el.opt.indexPlaceHolder);
            indexBox.empty();
            if(el.pages.length==1)
                return;
            var idxContainer =$("<div/>").addClass("idxContainer");
            indexBox.append(idxContainer);
            for (var i=0; i< el.pages.length;i++){
                var indexEl=$("<div/>").addClass("idxPage").attr("id","pageIdx_"+i).data("idx",i);
                indexEl.click(function(){
                    $.disclose.goTo(el,$(this).data("idx"),true);
                });
                idxContainer.append(indexEl);
            }
            $(".idxPage",indexBox).eq(el.page).addClass("sel");

            $.disclose.showProgress(el);
        },

        showProgress:function(el){
            clearInterval(el.progress);

            if($(el.opt.progressPlaceHolder).length==0)
                return;

            el.startTime= new Date().getTime();

            var progBox = $(el.opt.progressPlaceHolder);
            progBox.empty();
            var progBar = $("<div/>").addClass("progressBar");
            progBox.append(progBar);
            var dataTime = el.actualBanner.children().eq(0).data("time");

            var totTime = dataTime ? dataTime : el.opt.slideInterval;
            var progress = 0;
            el.progress=setInterval(function(){

                if(!el.opt.autoPlay || progress >= totTime){
                    clearInterval(el.progress);
                }

                var prop = (progBox.width()*progress)/totTime;
                progBar.css({width:prop});
                var getTime = new Date().getTime();
                progress= getTime - el.startTime;
            },1);
        },

        refresh:function(el){
            el.pages.children().css({height:$(el).height()});
            el.actualBanner.children().css({height:$(el).height()});
        }
    };

    $.fn.disclose = $.disclose.init;
    $.fn.getAnimation = $.disclose.getAnimation;

    /*UTILITIES*/

    $.fn.unselectable=function(){
        this.each(function(){
            $(this).css({
                "-moz-user-select": "none",
                "-khtml-user-select": "none",
                "user-select": "none"
            }).attr("unselectable","on");
        });
        return $(this);
    };

    $.fn.clearUnselectable=function(){
        this.each(function(){
            $(this).css({
                "-moz-user-select": "auto",
                "-khtml-user-select": "auto",
                "user-select": "auto"
            });
            $(this).removeAttr("unselectable");
        });
        return $(this);
    };

    $.normalizeTransform=function(css){
        var sfx = "";
        if ($.browser.webkit) {
            sfx = "-webkit-";
        } else if ($.browser.mozilla) {
            sfx = "-moz-";
        } else if ($.browser.opera) {
            sfx = "-o-";
        } else if ($.browser.msie) {
            sfx = "-ms-";
        }

        for(var o in css){
            if (o==="transform"){
                if(!$.browser.msie)
                    css[sfx+"transform"]=css[o];
                delete css[o];
            }
            if (o==="transform-origin"){
                if(!$.browser.msie)
                    css[sfx+"transform-origin"]=css[o];
                delete css[o];
            }
        }
        return css;
    };


    $.fn.swipe = function(opt) {
        var defaults = {
            time:600,
            diff:400,
            swipeLeft:function() {
            },
            swipeRight:function() {
            }
        };
        $.extend(defaults, opt);
        return this.each(function() {
            this.swipe = {sp:0,ep:0, s:0, e:0};

            this.addEventListener('touchstart', function(event) {
                if(event.touches.length>1){
                    this.abort=true;
                    return;
                }
                //event.preventDefault();
                var touch = event.touches[0];
                this.swipe.sp = touch.pageX;
                this.swipe.s = new Date().getTime();
            }, false);

            this.addEventListener('touchmove', function(event) {
                event.preventDefault();
            },false);

            this.addEventListener('touchend', function(event) {
                if(this.abort) {
                    this.abort=false;
                    return;
                }
                //event.preventDefault();
                var touch = event.changedTouches[0];
                this.swipe.ep = touch.pageX;

                if((parseFloat(new Date().getTime()) - parseFloat(this.swipe.s)) > defaults.time && event.touches.length==1)
                    return;
                if (this.swipe.ep > this.swipe.sp + defaults.diff) {
                    event.stopPropagation();
                    defaults.swipeRight(this);
                } else if (this.swipe.ep < this.swipe.sp - defaults.diff) {
                    event.stopPropagation();
                    defaults.swipeLeft(this);
                }
            }, false);
        })
    };


    $.fn.doubleTap = function(opt) {
        var defaults = {
            time:300,
            func:function(o) {}
        };
        $.extend(defaults, opt);
        return this.each(function() {
            this.tap = {s:0,e:0};
            this.addEventListener('touchstart', function(event) {
                if(this.tap.s>0 && (parseFloat(new Date().getTime()) - parseFloat(this.tap.s)) < defaults.time){
                    event.preventDefault();
                    event.stopPropagation();
                    defaults.func(this);
                }else
                    this.tap.s = new Date().getTime();
            }, false);
        })
    };

})(jQuery);