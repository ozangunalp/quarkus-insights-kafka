function uuidv4() {
    return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
}

const localUItem = localStorage.getItem("uid");
let uid = uuidv4();
if (localUItem) {
    uid = localUItem
} else {
    localStorage.setItem("uid", uid);
}

const localSItem = sessionStorage.getItem("sid");
let sid = uuidv4();
if (localSItem) {
    sid = localSItem;
} else {
    sessionStorage.setItem("sid", sid);
}

async function postData(url = '', data = {}) {
    // Default options are marked with *
    const response = await fetch(url, {
        method: 'POST',
        mode: 'cors',
        cache: 'no-cache',
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json'
        },
        redirect: 'follow',
        referrerPolicy: 'no-referrer',
        body: JSON.stringify(data) // body data type must match "Content-Type" header
    });
    return response;
}

function getXPathForElement(element) {
    const idx = (sib, name) => sib
        ? idx(sib.previousElementSibling, name||sib.localName) + (sib.localName == name)
        : 1;
    const segs = elm => !elm || elm.nodeType !== 1
        ? ['']
        : elm.id && document.getElementById(elm.id) === elm
            ? [`id("${elm.id}")`]
            : [...segs(elm.parentNode), `${elm.localName.toLowerCase()}[${idx(elm)}]`];
    return segs(element).join('/');
}

function getElementByXpath(path) {
    return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
}

function addCountToElement(event) {
    if (event.item1) {
        let element = getElementByXpath(event.item1);
        if (element) {
            if (element === document.body) {
                return;
            }
            element.style.position = "relative";
            element.setAttribute('data-count', event.item2);
            const hue = 180 - event.item2 * 2;
            element.style.setProperty('--background', "hsla(" + hue +", 100%, 50%, 0.5)");
            console.log(element);
        }
    }
}

function pointerEvent(e) {
    return {
        userId: uid,
        xpath: getXPathForElement(e.target),
    };
}

const circle = document.createElement("div");

function moveCircle(e) {
    circle.style.display = 'block';
    const halfWidth = circle.offsetWidth / 2;
    circle.style.top = (e.pageY - halfWidth) + "px";
    circle.style.left = (e.pageX - halfWidth) + "px";
    circle.classList.remove('appear-animation');
    void circle.offsetWidth;
    circle.classList.add('appear-animation');
}

function moveCircleAndPostData(e) {
    const pointer = pointerEvent(e);
    moveCircle(e);
    postData('clicks', pointer);
}

window.onload = function() {
    circle.id = "circle";
    if (window.location.search === '?view') {
        const source = new EventSource('/clicks-count');
        source.addEventListener('message', ev => {
            const events = JSON.parse(ev.data);
            console.log(events);
            if (events instanceof Array) {
                for (let i = 0; i < events.length; i++) {
                    addCountToElement(events[i]);
                }
            } else {
                addCountToElement(events);
            }
        });
    } else {
        document.body.append(circle);
        document.body.addEventListener("click", e => moveCircleAndPostData(e));
    }
}