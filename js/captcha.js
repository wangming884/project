/**
 * 前端 Canvas 验证码模块
 * 生成带干扰线和噪点的图形验证码，大小写不敏感比对
 */

const CaptchaGenerator = (function () {
    const CHARS = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789';
    const DEFAULT_LENGTH = 4;
    const DEFAULT_WIDTH = 120;
    const DEFAULT_HEIGHT = 40;

    function randomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    function randomColor(min, max) {
        const r = randomInt(min, max);
        const g = randomInt(min, max);
        const b = randomInt(min, max);
        return 'rgb(' + r + ',' + g + ',' + b + ')';
    }

    function generateText(length) {
        var text = '';
        for (var i = 0; i < length; i++) {
            text += CHARS.charAt(randomInt(0, CHARS.length - 1));
        }
        return text;
    }

    function draw(canvas, options) {
        options = options || {};
        var length = options.length || DEFAULT_LENGTH;
        var width = options.width || DEFAULT_WIDTH;
        var height = options.height || DEFAULT_HEIGHT;

        canvas.width = width;
        canvas.height = height;

        var ctx = canvas.getContext('2d');
        ctx.fillStyle = randomColor(220, 255);
        ctx.fillRect(0, 0, width, height);

        var text = generateText(length);
        var fontSize = Math.floor(height * 0.7);
        ctx.font = 'bold ' + fontSize + 'px "Courier New", monospace';
        ctx.textBaseline = 'middle';

        var charWidth = (width - 20) / length;
        for (var i = 0; i < text.length; i++) {
            ctx.save();
            var x = 10 + i * charWidth + charWidth / 2;
            var y = height / 2 + randomInt(-4, 4);
            var angle = randomInt(-25, 25) * (Math.PI / 180);
            ctx.translate(x, y);
            ctx.rotate(angle);
            ctx.fillStyle = randomColor(30, 120);
            ctx.fillText(text[i], -fontSize / 4, 0);
            ctx.restore();
        }

        for (var j = 0; j < 4; j++) {
            ctx.strokeStyle = randomColor(100, 200);
            ctx.lineWidth = randomInt(1, 2);
            ctx.beginPath();
            ctx.moveTo(randomInt(0, width), randomInt(0, height));
            ctx.lineTo(randomInt(0, width), randomInt(0, height));
            ctx.stroke();
        }

        for (var k = 0; k < 50; k++) {
            ctx.fillStyle = randomColor(0, 255);
            ctx.beginPath();
            ctx.arc(randomInt(0, width), randomInt(0, height), randomInt(1, 2), 0, 2 * Math.PI);
            ctx.fill();
        }

        return text;
    }

    function create(canvasEl, options) {
        var canvas = typeof canvasEl === 'string'
            ? document.getElementById(canvasEl)
            : canvasEl;

        if (!canvas || canvas.tagName !== 'CANVAS') {
            console.error('CaptchaGenerator: 需要一个 canvas 元素');
            return null;
        }

        var currentText = '';

        function refresh() {
            currentText = draw(canvas, options);
        }

        refresh();

        return {
            refresh: refresh,
            verify: function (input) {
                return currentText.toLowerCase() === String(input || '').trim().toLowerCase();
            },
            getText: function () {
                return currentText;
            },
            getCanvas: function () {
                return canvas;
            },
        };
    }

    return { create: create, draw: draw };
})();
