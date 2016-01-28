$(function($){
        var addToAll = false;
        var gallery = true;
        var titlePosition = 'inside';
        $(addToAll ? 'img' : 'img.fancybox').each(function(){
            var $this = $(this);
            var title = $this.attr('title');
            var src = $this.attr('data-big') || $this.attr('src');
            var a = $('<a href="#" class="fancybox"></a>').attr('href', src).attr('title', title);
            $this.wrap(a);
        });
        if (gallery)
            $('a.fancybox').attr('rel', 'fancyboxgallery');
        $('a.fancybox').fancybox({
            titlePosition: titlePosition
        });
        
        
        $( "input[name*='all']" ).click(function(e)
        		{
        	if($(this).attr('data-toggle')=='unchecked')
        		{
        		$(this).prop('checked', true);
        		e.stopPropagation();
        		$(this).attr('data-toggle','checked');
        		var col=$(this).val().replace("all","");
          		col=parseInt(col)+1;		  
        		$('td:nth-child('+col+') div input').prop('checked', true);	
        		}
        	else
        		{
        		$(this).prop('checked', false);
        		e.stopPropagation();
        		$(this).attr('data-toggle','unchecked');
        		var col=$(this).val().replace("all","");
          		col=parseInt(col)+1;		  
        		$('td:nth-child('+col+') div input').prop('checked', false);	
        		}
        });
        
        $( "td div input" ).click(function(e)
        		{
        	if($(this).attr('data-toggle')=='unchecked')
        	{
        		$(this).prop('checked', true);
        		e.stopPropagation();
        		$(this).attr('data-toggle','checked'); 
        		$(this).prop('checked', true);	
        	}
        	else
        	{
        		$(this).prop('checked', false);
        		e.stopPropagation();
        		$(this).attr('data-toggle','unchecked');  
        		$(this).prop('checked', false);	
        	}
        		});
        
    });
    $.noConflict();
    
 
    