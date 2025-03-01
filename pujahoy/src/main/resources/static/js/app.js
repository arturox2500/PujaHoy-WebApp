const sleep = ms => new Promise(r => setTimeout(r, ms));
let page = 1;
let noMorePosts = false;

async function cargarPosts() {
    console.log("presiona")
    if (noMorePosts) {
        return;
    }
    
    document.getElementById("spinnerY").style.display = "block"; // Mostrar el spinner
    document.getElementById("spinnerY").style.visibility = "visible";
    document.getElementById("load-more-yposts").style.display = "none";
    await sleep(300);
    try {
        const response = await fetch(`/usuario/producto_template?pagina=${page}`);
        const responseNext = await fetch(`/usuario/producto_template?pagina=${page+1}`);
        if (response.ok) {
            const nuevosPostsHTML = (await response.text()); 
            console.log(nuevosPostsHTML)
            if (nuevosPostsHTML != "") {
                console.log("YESSSU")
                document.getElementById("yourProdsRow").innerHTML += nuevosPostsHTML;
                document.getElementById("load-more-yposts").style.display = "block";
                if (!responseNext.ok || (await responseNext.text()).trim() === "") {
                    noMorePosts = true;
                    document.getElementById("load-more-yposts").style.display = "none";
                    document.getElementById("spinnerY").style.visibility = "hidden";
                }
            } 
            console.log(page)
            page++;
        }
    } catch (error) {
        console.error("Error al cargar los posts:", error);
    }

    document.getElementById("spinnerY").style.display = "none"; // Ocultar el spinner
    
}

document.addEventListener("DOMContentLoaded", function () {
    const btnY = document.getElementById("load-more-yposts");
    if (btnY) {
        btnY.addEventListener("click", cargarPosts);
    }
});


async function cargarPosts2() {
    console.log("presiona")
    if (noMorePosts) {
        return;
    }
    
    document.getElementById("spinnerWB").style.display = "block"; // Mostrar el spinner
    document.getElementById("spinnerWB").style.visibility = "visible";
    document.getElementById("load-more-WBposts").style.display = "none";
    await sleep(300);
    try {
        const response = await fetch(`/usuario/producto_template_compras?pagina=${page}`);
        const responseNext = await fetch(`/usuario/producto_template_compras?pagina=${page+1}`);
        if (response.ok) {
            const nuevosPostsHTML = await response.text(); 
            
            if (nuevosPostsHTML !== "") {
                document.getElementById("yourWBRow").innerHTML += nuevosPostsHTML;
                document.getElementById("load-more-WBposts").style.display = "block";
                if (!responseNext.ok || (await responseNext.text()).trim() === "") {                   
                    noMorePosts = true;
                    document.getElementById("load-more-WBposts").style.display = "none";
                    document.getElementById("spinnerWB").style.visibility = "hidden";
                }
            } 

            page++;
        }
    } catch (error) {
        console.error("Error al cargar los posts:", error);
    }

    document.getElementById("spinnerWB").style.display = "none"; // Ocultar el spinner
}

document.addEventListener("DOMContentLoaded", function () {
    const btnWB = document.getElementById("load-more-WBposts");
    if (btnWB) btnWB.addEventListener("click", cargarPosts2);
});



