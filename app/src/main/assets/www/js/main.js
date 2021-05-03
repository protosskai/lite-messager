function pushMessage(message_str) {
    $.get("push", {
        message: message_str
    })
}
const chunkSize = 1024 * 1024 * 10;//每次上传分片的大小
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
function asyncSendFile(file, url, success, fail) {
    let totalSize = file.size;
    let chunkQuantity = Math.ceil(totalSize / chunkSize); //分片总数
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
                success(i)
            } else {
                fail(i)
            }
        }
        let fd = new FormData();
        fd.append("sliceNumber", i)
        fd.append("data", blob)
        fd.append("fileName", file.name)
        xhr.send(fd);
    }
}

function successCallback(sliceNumber) {
    console.log(sliceNumber + " success!")
}

function failCallback(sliceNumber) {
    console.log(sliceNumber + " fail!")
}