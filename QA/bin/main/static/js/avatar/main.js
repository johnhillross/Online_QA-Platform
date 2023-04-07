$("#newavatar").on("change", function() {
    let fileReader = new FileReader(),
        fileType = this.files[0].type;
    fileReader.onload = function() {
        if (/^image/.test(fileType)) {
            // 读取结果在fileReader.result里面
            $("#img-avatar").attr('src', this.result);
        }
    }
    // 打印原始File对象
    console.log(this.files[0]);
    // base64方式读取
    fileReader.readAsDataURL(this.files[0]);    
});