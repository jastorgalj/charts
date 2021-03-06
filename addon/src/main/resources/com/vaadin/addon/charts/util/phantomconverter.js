/**
 * @license Highcharts JS v2.3.3 (2012-11-02)
 * 
 * (c) 2009-2011 Gert Vaartjes
 * 
 * License: www.highcharts.com/license
 * 
 * 
 * Modified 1/2013 at Vaadin Ltd - Adapted to fit better for usage in Vaadin
 * Charts. Uses server so that phantomjs instances can be recycled. Also now no need
 * to use temp files.
 */

/* get the arguments from the commandline and map them */

var system = require('system');
args = {};
for ( var i = 0; i < system.args.length; i += 1) {
	if (system.args[i].charAt(0) === '-') {
		args[system.args[i].substr(1, i.length)] = system.args[i + 1];
	}
}

var fs = require('fs');

function render(configstr) {

	var page = require('webpage').create(), HC = {}, pick, scaleAndClipPage, input, constr, callback, width, callbackStr, optionsStr, output, outputExtension, pdfOutput, svg, svgFile, svgElem, timer;

	HC.imagesLoaded = 'Highcharts.imagesLoaded:7a7dfcb5df73aaa51e67c9f38c5b07cb';
	window.imagesLoaded = false;

	page.onConsoleMessage = function(msg) {
		console.log(msg);
		/*
		 * Ugly hack, but only way to get messages out of the 'page.evaluate()'
		 * sandbox. If any, please contribute with improvements on this!
		 */
		if (msg === HC.imagesLoaded) {
			window.imagesLoaded = true;
		}
	};

	page.onAlert = function(msg) {
		console.log(msg);
	};

	pick = function() {
		var args = arguments, i, arg, length = args.length;
		for (i = 0; i < length; i += 1) {
			arg = args[i];
			if (arg !== undefined && arg !== null && arg !== 'null'
					&& arg != '0') {
				return arg;
			}
		}
	};

	/* scale and clip the page */
	scaleAndClipPage = function(svg, pdf) {
		/*
		 * param: svg: The scg configuration object param: pdf: boolean, if true
		 * set papersize
		 */

		var zoom = 1, pageWidth = pick(args.width, svg.width), clipwidth, clipheight;

		if (parseInt(pageWidth, 10) == pageWidth) {
			zoom = pageWidth / svg.width;
		}

		/*
		 * set this line when scale factor has a higher precedence scale has
		 * precedence : page.zoomFactor = args.scale ? zoom * args.scale : zoom;
		 */

		/*
		 * args.width has a higher precedence over scaling, to not break
		 * backover compatibility
		 */
		page.zoomFactor = args.scale && args.width == undefined ? zoom
				* args.scale : zoom;

		clipwidth = svg.width * page.zoomFactor;
		clipheight = svg.height * page.zoomFactor;

		/* define the clip-rectangle */
		page.clipRect = {
			top : 0,
			left : 0,
			width : clipwidth,
			height : clipheight
		};

		/*
		 * for pdf we need a bit more paperspace in some cases for example
		 * (w:600,h:400), I don't know why.
		 */
		if (pdf) {
			page.paperSize = {
				width : clipwidth,
				height : clipheight + 2
			};
		}
	};

	callback = args.callback;
	width = args.width;

	// load necessary libraries
	page.injectJs(args["jsstuff"]);

	// load callback from file
	if (callback !== undefined) {
		callbackStr = fs.read(callback);
	}

	var renderer = function(width, str, callbackStr) {
		opt = JSON.parse(str);

		var imagesLoadedMsg = 'Highcharts.imagesLoaded:7a7dfcb5df73aaa51e67c9f38c5b07cb', chart, nodes, nodeIter, elem, opacity;

		// dynamic script insertion
		function loadScript(varStr, codeStr) {
			var $script = $('<script>').attr('type', 'text/javascript');
			$script.html('var ' + varStr + ' = ' + codeStr);
			document.getElementsByTagName("head")[0].appendChild($script[0]);
		}

		// are all images loaded in time?
		function logCounter(counter) {
			counter -= 1;
			if (counter < 1) {
				console.log(imagesLoadedMsg);
			}
		}

		function loadImages() {
			// are images loaded?
			var $images = $('svg image'), counter, i, img;

			if ($images.length > 0) {

				counter = $images.length;

				for (i = 0; i < $images.length; i += 1) {
					img = new Image();
					img.onload = logCounter(counter);
					/*
					 * force loading of images by setting the src attr.
					 */
					img.src = $images[i].getAttribute('href');
				}
			} else {
				// no images set property to all images
				// loaded
				console.log(imagesLoadedMsg);
			}
		}

		// formatJsJson is needed to fix javascript
		// formatters in json
		formatJsJson(opt);

		if (callbackStr !== 'undefined') {
			loadScript('callback', callbackStr);
		}

		document.body.style.margin = '0px';
		var cont = document.createElement("div");
		document.body.appendChild(cont);

		// disable animations
		Highcharts.SVGRenderer.prototype.Element.prototype.animate = Highcharts.SVGRenderer.prototype.Element.prototype.attr;

		if (!opt.chart) {
			opt.chart = {};
		}

		opt.chart.renderTo = cont;

		console.log(opt);

		// check if width is set. Order of precedence:
		// args.width, options.chart.width and 600px

		// OLD. options.chart.width = width ||
		// options.chart.width || 600;
		// Notice we don't use commandline parameter width
		// here. Commandline parameter width is used for
		// scaling.
		opt.chart.width = (opt.exporting && opt.exporting.sourceWidth)
				|| opt.chart.width || 600;
		opt.chart.height = (opt.exporting && opt.exporting.sourceHeight)
				|| opt.chart.height || 400;

		chart = new Highcharts.Chart(opt, callback);

		// ensure images are all loaded
		loadImages();

		return {
			html : cont.firstChild.innerHTML,
			width : chart.chartWidth,
			height : chart.chartHeight
		};

	};
	// load chart in page and return svg height and width
	svg = page.evaluate(renderer, width, configstr, callbackStr);

	page.close();
	return svg.html;

}

// FIXME implement with STDIN/OUT when PhantomJS 1.9 is out
var server = require('webserver').create();
var service = server.listen('127.0.0.1:' + args.port, function(request,
		response) {
	var configstr = request.post;
	response.statusCode = 200;
	try {
		response.write(render(configstr));
	} catch (e) {
		response.write("Render failed:\n" + e);
	}
	response.close();
});

console.log("OK, ready.");
