function pushMessage(message_str) {
    $.get("push", {
        message: message_str
    })
}

// 发送开始上传文件请求
function sendStartRequest(file, url) {
    const LENGTH = 1024 * 1024;//每次上传分片的大小
    let fileName = file.name    // 文件名
    let size = file.size        // 文件大小（字节）
    let sliceNumber = Math.ceil(size / LENGTH) //总共的分片数量
    let xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    let fd = new FormData();
    fd.append("fileName", fileName);
    fd.append("size", size);
    fd.append("sliceSize", LENGTH)
    fd.append("sliceNumber", sliceNumber)
    xhr.send(fd);
}



function asyncSendFile(file, url) {
    let chunkSize = 1024 * 1024; //每片1M大小
    let totalSize = file.size;
    let chunkQuantity = Math.ceil(totalSize / chunkSize); //分片总数
    for (let i = 0; i < chunkQuantity; i++) {
        let blob = file.slice(i * chunkSize, (i + 1) * chunkSize);
        let xhr = new XMLHttpRequest();
        xhr.open("POST", url);
        let fd = new FormData();
        fd.append("sliceNumber", i);
        fd.append("data", blob);
        fd.append("fileName", file.name)
        xhr.send(fd);
    }
}