(function($) {
	$.mbMomentum={
		name:"mb.scroll",
		author:"Matteo Bicocchi",
		version:"1.0",
		defaults:{
			scrollX:true,
			scrollY:true,
			momentum:true,
			fps:25,
			maxInertia:80,
			bounce:false
		},

		init:function(opt){
			var hasTouch = 'ontouchstart' in window;
			var events={};
			events.start = hasTouch ? "touchstart" : "mousedown";
			events.move = hasTouch ? "touchmove" : "mousemove";
			events.end = hasTouch ? "touchend" : "mouseup";

			return this.each(function(){
				var el= this;
				var $el= $(this);
				el.opt = {};
				el.inertia={};
				el.inertia.x=0;
				el.inertia.y=0;
				el.momentum={};
				el.margin={};
				el.hasTouch=hasTouch;

        setInterval(function(){$.mbMomentum.refresh(el)},500);

				$.extend (el.opt, $.mbMomentum.defaults,opt);

				el.opt.scrollX = $("#tree").outerWidth() <= $el.outerWidth() ? false :  el.opt.scrollX;
				el.opt.scrollY = $("#tree").outerHeight() <= $el.outerHeight() ? false :  el.opt.scrollY;
				$el.css({overflow:"hidden"});

				if(!el.hasTouch){
					$el.bind(events.start,function(e){$.mbMomentum.start(e,el);});
					$(document).bind(events.move, function(e){$.mbMomentum.move(e,el);});
					$(document).bind(events.end, function(){$.mbMomentum.end(el);});
				}else{
					el.addEventListener(events.start, function(e){$.mbMomentum.start(e,el);}, false);
					document.addEventListener(events.move, function(e){$.mbMomentum.move(e,el);}, false);
					document.addEventListener(events.end, function(){$.mbMomentum.end(el);}, false);
				}

        console.debug(el, 2)

      })
		},


		start:function(e, el){
			var $el= $(el);

			clearTimeout(el.momentum.timer);

			if($(e.target).is("textarea, input, .selectable, li")){
				return false;
			}
			
			el.busy=true;

			if(el.hasTouch){
				e = e.touches[0];
			}else{
				e.preventDefault();
			}

			el.startX = el.scrollX = e.clientX;
			el.startY = el.scrollY = e.clientY;

			el.maxX=0;
			el.maxY=0;
			el.minX= -($("#tree").outerWidth() - $el.outerWidth());
			el.minY= -($("#tree").outerHeight()- $el.outerHeight());
			$.mbMomentum.checkForInertia(el);
			return false;
		},

		move:function(e, el){

			if($(e.target).is("textarea, input, .selectable")){
				return false;
			}
      
      e.preventDefault();

			if(el.hasTouch){
				e = e.touches[0];
			}

			var $el= $(el);
			$el.unselectable();
			el.x= e.clientX;
			el.y= e.clientY;


			if(el.busy){
				$.mbMomentum.scroll(el);
			}
		},

		end:function(el){

			if(!el.busy)
				return;

			var $el= $(el);
			$el.clearUnselectable();
			el.busy=false;

			el.endX= el.x;
			el.endY= el.y;

			if(el.opt.momentum)
				$.mbMomentum.momentum(el);

			clearTimeout(el.inertia.timer);
		},

		scroll:function(el){
			var $el = $(el);
			var elW = $("#tree").outerWidth();
			var elH = $("#tree").outerHeight();

			var ml = parseFloat($("#tree").css("margin-left"));
			var mt = parseFloat($("#tree").css("margin-top"));

			$.mbMomentum.checkForOutbox(el);

			if(el.opt.scrollX)
				ml += (el.x - el.scrollX)/el.momentum.frictionX;
			if(el.opt.scrollY)
				mt += (el.y - el.scrollY)/el.momentum.frictionY;

			el.margin={marginLeft:ml, marginTop:mt};

      $("#tree").css(el.margin);
			el.scrollX=el.x;
			el.scrollY=el.y;
		},

		checkForOutbox:function(el){

			var ml= el.margin.marginLeft ? el.margin.marginLeft : 0;
			var mt= el.margin.marginTop ? el.margin.marginTop : 0;

			el.outOfTheBoxH = (ml > el.maxX) ? -1 :(ml < el.minX) ? 1 : 0;
			el.outOfTheBoxV = (mt > el.maxY)? -1 : (mt < el.minY) ? 1 : 0;
			el.momentum.frictionX = ml > el.maxX+50 ? ml/5 : ml < el.minX-50 ? (el.minX-ml)/5 : 1;
			el.momentum.frictionY = mt > el.maxY+50 ? mt/5 : mt < el.minY-50 ? (el.minY-mt)/5 : 1;
		},
		momentum:function(el){
			if(el.inertia.speedX != 0 || el.inertia.speedY != 0 || el.outOfTheBoxH ||  el.outOfTheBoxV){
				el.momentum.targetX = el.opt.scrollX ? el.margin.marginLeft - ((el.startX - el.endX ) * Math.abs(el.inertia.speedX/el.opt.fps)) :el.margin.marginLeft;
				el.momentum.targetY = el.opt.scrollY ? el.margin.marginTop - ((el.startY - el.endY) * Math.abs(el.inertia.speedY/el.opt.fps)) :el.margin.marginTop;
				el.momentum.speed=el.opt.fps;
			}else{
				return;
			}
			$.mbMomentum.applyMomentum(el);
		},

		applyMomentum:function(el){
			if (el.busy)
				return;

			if(el.outOfTheBoxH || el.outOfTheBoxV){
				if (el.outOfTheBoxH){
					el.momentum.targetX = el.outOfTheBoxH == -1 ? el.maxX : el.minX;
					el.momentum.speed=2;
					el.momentum.frictionX=2;
				}
				if (el.outOfTheBoxV){
					el.momentum.targetY = el.outOfTheBoxV == -1 ? el.maxY : el.minY;
					el.momentum.speed=2;
					el.momentum.frictionY=2;
				}
			}

			var stepX =  Math.floor(( el.margin.marginLeft - el.momentum.targetX)/el.momentum.speed)/el.momentum.frictionX;
			var stepY =  Math.floor(( el.margin.marginTop - el.momentum.targetY)/el.momentum.speed)/el.momentum.frictionY;


			$.mbMomentum.checkForOutbox(el);

			el.momentum.timer = setTimeout(function(){
				var $el = $(el);
				el.margin = {marginLeft:el.margin.marginLeft-stepX, marginTop:el.margin.marginTop-stepY};
        $("#tree").css(el.margin);
				if(stepX==0 && stepY==0)
					return;
				$.mbMomentum.applyMomentum(el);
			},1000/el.opt.fps)
		},

		checkForInertia:function(el){
			if(el.busy){
				el.inertia.timer = setTimeout(function(){
					el.inertia.speedX = el.x - el.inertia.x;
					el.inertia.speedY = el.y - el.inertia.y;

					el.inertia.speedX = Math.abs(el.inertia.speedX) < el.opt.maxInertia ?  el.inertia.speedX : el.inertia.speedX<0 ? -el.opt.maxInertia : el.opt.maxInertia;
					el.inertia.speedY = Math.abs(el.inertia.speedY) < el.opt.maxInertia ?  el.inertia.speedY : el.inertia.speedY<0 ? -el.opt.maxInertia : el.opt.maxInertia;

					el.inertia.x=el.x;
					el.inertia.y=el.y;

					$.mbMomentum.checkForInertia(el);

				},(1000/el.opt.fps));
			}
		},
		update:function(){
			var el= this.get(0);
			$.mbMomentum.checkForOutbox(el);
/*
			el.container.css({"width":"auto", height:"auto"});
			el.container.css({"width":el.container.contents().outerWidth(), height: el.container.contents().outerHeight() });
*/
		},
    refresh: function(el){
      var $el = $(el);
      el.opt.scrollX = $el.find("ul").width() <= $el.outerWidth() ? false :  el.opt.scrollX;
      el.opt.scrollY = $("#tree").outerHeight() <= $el.outerHeight() ? false :  el.opt.scrollY;

      el.opt.scrollX = true;
      el.opt.scrollY = true;

      console.debug($el.find("ul").width());
    }
	};


	$.fn.mbMomentum=$.mbMomentum.init;
	$.fn.mbMomentumUpdate=$.mbMomentum.update;

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
})(jQuery);
