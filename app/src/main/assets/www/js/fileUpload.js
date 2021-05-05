function FileUpload(file, before, success, fail) {


    const chunkSize = 1024 * 1024;//每次上传分片的大小
    const workers = 5;
    let self = this
    this.totalSize = file.size;  // 文件大小
    this.chunkQuantity = Math.ceil(this.totalSize / chunkSize); //分片总数
    this.currentXhrQueue = new Array(workers)   // 当前正在处理的请求队列
    this.totalXhrQueue = new Array(this.chunkQuantity)   // 所有的请求队列
    this.totalFormDataQueue = new Array(this.chunkQuantity) // 请求参数队列
    this.interval = null //创建的定时器对象
    this.runFlag = false    // 控制上传暂停
    before(this.totalSize, this.chunkQuantity)
    // 初始化队列
    for (let i = 0; i < this.chunkQuantity; i++) {
        let start = 0;
        let end = 0;
        if (i == this.chunkQuantity - 1) {
            start = i * chunkSize;
            end = this.totalSize;
        } else {
            start = i * chunkSize;
            end = (i + 1) * chunkSize;
        }
        let blob = file.slice(start, end);
        let result = createXhr(i, blob, "/fileUpload", success, fail)
        this.totalXhrQueue[i] = result[0]
        this.totalFormDataQueue[i] = result[1]
    }
    for (let i = 0; i < workers; i++) {
        this.currentXhrQueue[i] = null;
    }



    this.start = function () {
        this.runFlag = true
        sendStartRequest(file, "/startFileUpload")
        // 创建定时器来驱动程序运行
        this.interval = setInterval(next, 200)
    }

    this.pause = function () {
        this.runFlag = false
        clearInterval(this.interval)
    }

    this.stop = function () {
        this.runFlag = false
        clearInterval(this.interval)
    }

    this.resume = function () {
        this.runFlag = true
        // 创建定时器来驱动程序运行
        this.interval = setInterval(next, 200)
    }

    // 创建XMLHttpRequest对象
    function createXhr(index, blob, url, success, fail) {
        let xhr = new XMLHttpRequest();
        xhr.open("POST", url);
        xhr.onreadystatechange = function () {
            let status = xhr.status;
            if (status === 0 || (status >= 200 && status < 400)) {
                success(index, self.chunkQuantity)
            } else {
                fail(index, self.chunkQuantity)
            }
        }
        let fd = new FormData();
        fd.append("sliceNumber", index)
        fd.append("data", blob)
        fd.append("fileName", file.name)
        return [xhr, fd]
    }

    // 发送开始上传文件请求
    function sendStartRequest(file, url) {
        let fileName = file.name    // 文件名
        let size = file.size        // 文件大小（字节）
        let sliceNumber = Math.ceil(size / chunkSize) //总共的分片数量
        let xhr = new XMLHttpRequest()
        xhr.open("POST", url, false)
        let fd = new FormData()
        fd.append("fileName", fileName)
        fd.append("size", size)
        fd.append("sliceSize", chunkSize)
        fd.append("sliceNumber", sliceNumber)
        xhr.send(fd);
    }

    function next() {
        if (!self.runFlag) {
            clearInterval(self.interval)
            return
        }
        // 检查处理队列是否有空闲（请求已完成或为空）
        let emptyIndexs = new Array()
        for (let i = 0; i < workers; i++) {
            if (self.currentXhrQueue[i] == null) {
                emptyIndexs.push(i)
            } else if (self.currentXhrQueue[i].status === 0 || (self.currentXhrQueue[i].status >= 200 && self.currentXhrQueue[i].status < 400)) {
                emptyIndexs.push(i)
            }
        }
        // 取出总队列队尾的空闲数量个请求放到队列里面, 并启动
        for (let i = 0; i < emptyIndexs.length; i++) {
            if (self.totalXhrQueue.length == 0) {
                self.runFlag = false
                return
            }
            let lastXhr = self.totalXhrQueue.pop()
            let lastFormData = self.totalFormDataQueue.pop()
            let index = emptyIndexs[i]
            self.currentXhrQueue[index] = lastXhr
            self.currentXhrQueue[index].send(lastFormData)
        }
    }


}