// 화면 -> 서버 , 첨부 이미지 업로드
async function uploadToServer (formObj){
    console.log(formObj)

    const response = await axios({
        method : 'post',
        url : '/upload',
        data : formObj,
        headers : {
            'Content-Type' : 'multipart/form-data'
        }
    });
    return response.data
}

// 화면 -> 서버, 첨부 이미지 삭제
async function removeFileToServer (uuid, fileName){
    console.log("upload.js ,removeFileToServer 삭제 작업 확인 ,removeFileList 3 : " + removeFileList)
    console.log("upload.js ,removeFileToServer 삭제 작업 확인 ," , uuid)
    console.log("upload.js ,removeFileToServer 삭제 작업 확인 ,", fileName)

    //오타 수정 , delete -> remove
    const response = await axios.delete(
        `/remove/${uuid}_${fileName}`
    );
    return response.data
}