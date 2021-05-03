function pushMessage(message_str) {
    $.get("push", {
        message: message_str
    })
}
const chunkSize = 1024 * 1024;//每次上传分片的大小
// 发送开始上传文件请求
function sendStartRequest(file, url) {
    let fileName = file.name    // 文件名
    let size = file.size        // 文件大小（字节）
    let sliceNumber = Math.ceil(size / chunkSize) //总共的分片数量
    let xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    let fd = new FormData();
    fd.append("fileName", fileName);
    fd.append("size", size);
    fd.append("sliceSize", chunkSize)
    fd.append("sliceNumber", sliceNumber)
    xhr.send(fd);
}


// 分片异步上传文件
function asyncSendFile(file, url, before, success, fail) {
    let totalSize = file.size;
    let chunkQuantity = Math.ceil(totalSize / chunkSize); //分片总数
    before(totalSize, chunkQuantity)
    for (let i = 0; i < chunkQuantity; i++) {
        let start = 0;
        let end = 0;
        if (i == chunkQuantity - 1) {
            start = i * chunkSize;
            end = totalSize;
        } else {
            start = i * chunkSize;
            end = (i + 1) * chunkSize;
        }
        let blob = file.slice(start, end);
        let xhr = new XMLHttpRequest();
        xhr.open("POST", url);
        xhr.onreadystatechange = function () {
            let status = xhr.status;
            if (status === 0 || (status >= 200 && status < 400)) {
                success(i, chunkQuantity)
            } else {
                fail(i, chunkQuantity)
            }
        }
        let fd = new FormData();
        fd.append("sliceNumber", i)
        fd.append("data", blob)
        fd.append("fileName", file.name)
        xhr.send(fd);
    }
}


function Progress($container, min_value, max_value) {
    let container;
    if ($container != null) {
        container = $container;
    }
    let _id = "progress_bar" + new Date().getTime(); //progressBar随机id编号
    this.create = function (min, max, now) {
        container.append('<div class="progress" id="' + _id + '"><div class="progress-bar" role="progressbar" aria-valuenow="' + now + '" aria-valuemin="' + min + '" aria-valuemax="' + max + '"></div></div>');
    }
    this.update = function (min, max, now_value) {
        $("#" + _id).remove();
        this.create(min, max, now_value)
        let i = now_value / (max - min) * 100
        $("#" + _id + " .progress-bar").css({ "width": i + "%" })
    }
}


//如果在规定时间内都没有完成进度条,则停留在90%地方,一旦完成立刻到100%
//写在ajax请求执行开始处进行创建,执行完成后执行完成进度条进度为100%
//定义进度条类
//提供构建/展示/销毁等工作
//container为要包含进入条展示容器
// function ProgressBar($container) {

//     var self = this;

//     var container;

//     if ($container != null) {
//         container = $container;
//     }

//     var interval; //创建的周期函数对象
//     var _id = "progress_bar" + new Date().getTime(); //progressBar随机id编号

//     self.setContainer = function (_container) {
//         container = _container;
//     }

//     //为当前容器加入progress
//     self.createProgressBar = function () {
//         container.append('<div class="progress" id="' + _id + '"><div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"><span class="proText"></span></div></div>');
//     }

//     self.beginProcessBar = function () {
//         var i = 0;
//         interval = setInterval(function () {
//             i += 10;
//             if (i <= 90) {
//                 $("#" + _id + " .progress-bar").css({ "width": i + "%" });
//                 $("#" + _id + " .proText").text(i + '%');
//             }
//         }, 1000);
//     }

//     self.showProcessBar = function () {
//         self.createProgressBar();
//         self.beginProcessBar();
//     }

//     self.finishProcessBar = function () {
//         if (interval != null) {
//             $("#" + _id + " .progress-bar").css({ "width": "100%" });
//             $("#" + _id + " .proText").text('100%');
//             clearInterval(interval);
//         }
//     }

//     self.destroyProcessBar = function () {
//         $("#" + _id).remove();
//     }

//     return self;
// }