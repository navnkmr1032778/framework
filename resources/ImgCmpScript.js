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
        
        function onAjaxReturn(data,status,xhr)
		  {
		  	alert('images updated');
		  }
		  function onAjaxFail(xhr, status, error)
		  {
		  	alert('failed in updating images');
		  }
		 $('button').click(function sendToServlet()
		  {
			  var dataToUpdate={};
			  $('td input[type="radio"]:checked').each(function(i,e)
					  {
				  dataToUpdate[$(e).attr('name')]=$(e).val();
					  });
			  var stringifyData=JSON.stringify(dataToUpdate);
			  console.log(stringifyData);
			  $.ajax({
				  'url':'http://localhost:8080/project/example',
				  'dataType':'json',
				  'data':{
					  'rawData':stringifyData
				  },
				  'method':'POST',
				  'success':onAjaxReturn,
				  'error':onAjaxFail
			  });
		  });
        
    });
    $.noConflict();
    
 
    