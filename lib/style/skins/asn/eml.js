//This javascript file is to extend some eml front end behaviour

$(document).ready(function(){

        getDOI($('#documentId').html());
    	$.linkify($('.abstract > dd > div > p:first'));
    	$('.abstract > dd > div > p:first').truncate({max_length: 300});
    	$('.centity > dd > dl > dd:first').truncate({max_length: 300});

});


function getDOI(docId){
var vurl = MINTSERVICE + "/" + MINTSERVICESYSID + "/" +docId + "/";
 $.ajax({type:"GET", url: vurl, dataType:"jsonp", jsonpCallback:JSONPCALLBACK,
        error: function( jqXHR, textStatus, errorThrown ) {
            if (jqXHR.status === 0) {
                //console.log('Can\'t connect.\n Verify Network.');
            } else if (jqXHR.status == 404) {
                //console.log('Requested page not found. [404]');
            } else if (jqXHR.status == 500) {
                //console.log('Internal Server Error [500].');
            } else if (errorThrown === 'parsererror') {
                //console.log('Requested JSON parse failed.');
            } else if (errorThrown === 'timeout') {
                //console.log('Time out error.');
            } else if (errorThrown === 'abort') {
                //console.log('Ajax request aborted.');
            } else {
                //console.log('Uncaught Error.\n' + jqXHR.responseText);
            }
          }, // end error handler
        success: function(data) {
            if (data) {
                var identUrl = '<a href="http://dx.doi.org/' + data.identifier +'">'+ data.identifier +'</a>';
                $("#doiIdentifier").html('DOI:&#160;' + identUrl);
            }
        }});

}

(function($) {
  $.linkify = function(el) {
    var inputText = el.html();

    //URLs starting with http://, https://, or ftp://
    var replacePattern1 = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
    var replacedText = inputText.replace(replacePattern1, '<a href="$1" target="_blank">$1</a>');

    //URLs starting with www. (without // before it, or it'd re-link the ones done above)
    var replacePattern2 = /(^|[^\/])(www\.[\S]+(\b|$))/gim;
    replacedText = replacedText.replace(replacePattern2, '$1<a href="http://$2" target="_blank">$2</a>');

    //Change email addresses to mailto:: links
    var replacePattern3 = /[a-z0-9!#$%&'*+\/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/gim;
    replacedText = replacedText.replace(replacePattern3, '<a href="mailto:$&">$&</a>');

    el.html(replacedText);
  }
})(jQuery);

// HTML Truncator for jQuery
// by Henrik Nyh <http://henrik.nyh.se> 2008-02-28.
// Free to modify and redistribute with credit.

(function($) {

  var trailing_whitespace = true;

  $.fn.truncate = function(options) {

    var opts = $.extend({}, $.fn.truncate.defaults, options);
    
    $(this).each(function() {

      var content_length = $.trim(squeeze($(this).text())).length;
      if (content_length <= opts.max_length)
        return;  // bail early if not overlong

      var actual_max_length = opts.max_length - opts.more.length - 3;  // 3 for " ()"
      var truncated_node = recursivelyTruncate(this, actual_max_length);
      var full_node = $(this).hide();

      truncated_node.insertAfter(full_node);
      
      findNodeForMore(truncated_node).append(' ...<a class="morelink" href="#show more content">'+opts.more+'</a>');
      findNodeForLess(full_node).append(' <a class="morelink" href="#show less content">'+opts.less+'</a>');
      
      truncated_node.find('a:last').click(function() {
        truncated_node.hide(); full_node.show(); return false;
      });
      full_node.find('a:last').click(function() {
        truncated_node.show(); full_node.hide(); return false;
      });

    });
  }

  // Note that the " (more)" bit counts towards the max length ‰ so a max
  // length of 10 would truncate "1234567890" to "12 (more)".
  $.fn.truncate.defaults = {
    max_length: 100,
    more: 'more',
    less: 'less'
  };

  function recursivelyTruncate(node, max_length) {
    return (node.nodeType == 3) ? truncateText(node, max_length) : truncateNode(node, max_length);
  }

  function truncateNode(node, max_length) {
    var node = $(node);
    var new_node = node.clone().empty();
    var truncatedChild;
    node.contents().each(function() {
      var remaining_length = max_length - new_node.text().length;
      if (remaining_length == 0) return;  // breaks the loop
      truncatedChild = recursivelyTruncate(this, remaining_length);
      if (truncatedChild) new_node.append(truncatedChild);
    });
    return new_node;
  }

  function truncateText(node, max_length) {
    var text = squeeze(node.data);
    if (trailing_whitespace)  // remove initial whitespace if last text
      text = text.replace(/^ /, '');  // node had trailing whitespace.
    trailing_whitespace = !!text.match(/ $/);
    var text = text.slice(0, max_length);
    // Ensure HTML entities are encoded
    // http://debuggable.com/posts/encode-html-entities-with-jquery:480f4dd6-13cc-4ce9-8071-4710cbdd56cb
    text = $('<div/>').text(text).html();
    return text;
  }

  // Collapses a sequence of whitespace into a single space.
  function squeeze(string) {
    return string.replace(/\s+/g, ' ');
  }
  
  // Finds the last, innermost block-level element
  function findNodeForMore(node) {
    var $node = $(node);
    var last_child = $node.children(":last");
    if (!last_child) return node;
    var display = last_child.css('display');
    if (!display || display=='inline') return $node;
    return findNodeForMore(last_child);
  };

  // Finds the last child if it's a p; otherwise the parent
  function findNodeForLess(node) {
    var $node = $(node);
    var last_child = $node.children(":last");
    if (last_child && last_child.is('p')) return last_child;
    return node;
  };

})(jQuery);

