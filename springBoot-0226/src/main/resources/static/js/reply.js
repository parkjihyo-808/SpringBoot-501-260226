async function get1(bno) {
    // 화면에서 , axios 도구 이용해서, 스브프링 부트 서버에 비동기 통신 호출해서,
    // 게시글에 대한 , 댓글 목록 조회.
    const result = await axios.get(`/replies/list/${bno}`)
    // 데이터를 받아오는지 여부를 확인.
    // console.log(result)
    return result.data
}

// 비동기 통신으로 댓글의 목록 내용을 받아오는 함수
async function getList({bno,page,size,goLast}) {
    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size}})
    // 여기 안에, 댓글의 목록인 dtoList 가 포함되어 있다.

    // 맨마지막으로 이동 준비,
    if(goLast) {
        const total = result.data.total
        const lastPage= parseInt(Math.ceil(total/size))
        return getList({bno:bno, page:lastPage, size:size})

    }

    return result.data
}

// 화면에 출력하기 위한 함수, : 그림을 그리는 함수
// function printReplies(page, size, goLast) {
//     // 임시 테스트, 각자 게시글 번호에서, 댓글이 있는 부모 게시글 번호 이용하기.
//     const bnoValue = typeof bno !== 'undefined' ? bno : 125;
//     getList({bno: bnoValue, page, size, goLast}).then(
//
//         data => {
//             // 먼저 댓글 목록을 그리기
//             // 이 함수는 read.html 내부에 존재함.
//             printList(data.dtoList)
//             // 댓글의 페이지네이션 그려주는 함수 호출
//             printPages(data)
//         }
//     )
// }

// 댓글 목록 그리기 함수 수정,
async function getList({bno, page, size, goLast}){
    // 서버로부터 응답 받은 , 댓글의 목록입니다. (bno 부모 게시글에 따른 댓글 목록)
    const result = await axios.get(`/replies/list/${bno}`, {params: {page,size}})
    if(goLast) {
        const total = result.data.total
        const lastPage = parseInt(Math.ceil(total/size))
        return getList({bno:bno, page:lastPage, size:size})
    }
    return result.data
}


// 위에서, 화면을 그려주는 함수 호출해서, 실제 그림 그리기.
// 임의로 그리기.
// 마지막 매개변수 부분이, 댓글의 마지막 페이지로 이동하는 요소
// printReplies(1,10,true)


// 댓글 등록 함수
async function addReply(replyObj){
    // 오타수정
    const response = await axios.post(`/replies/`, replyObj)
    return response.data
}

// 댓글 조회 함수
async function getReply(rno){
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}

// 댓글 수정 함수
async function modifyReply(replyObj) {
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
}

// 댓글 삭제 함수
async function removeReply(rno) {
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}

