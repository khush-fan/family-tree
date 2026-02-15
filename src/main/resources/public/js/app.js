const API = "http://localhost:8081";
let family;

async function loadTree() {
    const tree = document.getElementById('tree');
    try {
        tree.innerHTML = `<div style="padding: 40px; text-align: center;">⏳ Загрузка дерева...</div>`;
        const response = await fetch(`${API}/tree`);

        if (!response.ok)
            throw new Error(`Ошибка: ${response.status}`);

        let data = await response.json();
        data = cleanParentIds(cleanParentIds(data.nodes));
        console.log("Данные: ", data);

        if (!family) {
            console.log("Создание древа")
            family = createFamilyTree(tree, data || []);
        } else {
            console.log("Древо существует")
            family.load(data || []);
        }

        if (!data || data.length === 0) {
            const firstNode = {
                id: 1,
                name: "Первый человек",
                gender: "male",
            };
            await addNode(firstNode);
            return;
        }

        return data;

    } catch (error) {
        console.log("Произошла ошибка при загрузке древа...");
        tree.innerHTML = `
            <div style="padding: 40px; text-align: center; color: #721c24; background: #f8d7da; border-radius: 10px;">
                <h3>❌ Ошибка загрузки</h3>
                <p>${error.message}</p>
                <button onclick="loadTree()" style="margin-top: 20px; padding: 10px 20px; background: #2196F3; color: white; border: none; border-radius: 5px; cursor: pointer;">
                    🔄 Попробовать снова
                </button>
            </div>
        `;
        return null;
    }
}

function createFamilyTree(container, nodes) {
    let options = getOptions();
    return new FamilyTree(container, {
        nodes: nodes,
        mouseScrool: FamilyTree.none,
        scaleInitial: options.scaleInitial,
        mode: 'dark',
        template: 'hugo',
        roots: [],

        nodeMenu: {
            details: {text: "Подробности"},
            edit: {text: "Редактировать"},
            remove: {
                text: "Удалить",
                onClick: deleteNode
            }
        },
        nodeCircleMenu: {
            // addParentNode: {
            //     mother: "sdfsd",
            // }
        },
        // nodeContextMenu: {
        //     edit: {text: "Edit", icon: FamilyTree.icon.edit(18, 18, '#039BE5')},
        // },
        nodeTreeMenu: false,
        nodeBinding: {
            field_0: 'name',
            field_1: 'born',
            img_0: 'photo',
        },
        enableSearch: false,
        editForm: {
            titleBinding: "name",
            photoBinding: "photo",
            generateElementsFromFields: false,
            elements: [
                {type: 'textbox', label: 'Имя', binding: 'name'},
                {type: 'date', label: 'Дата рождения', binding: 'born'},
                {type: 'textbox', label: 'Фото', binding: 'photo', btn: 'Загрузить'},
            ],
            cancelBtn: "Отмена",
            saveAndCloseBtn: "Сохранить и закрыть"
        },
    });
}

function getOptions() {
    const searchParams = new URLSearchParams(window.location.search);
    let fit = searchParams.get('fit');
    let enableSearch = true;
    let scaleInitial = 1;
    if (fit == 'yes') {
        enableSearch = false;
        scaleInitial = FamilyTree.match.boundary;
    }
    return {enableSearch, scaleInitial};
}

function setupEventListeners() {
    const zoomInBtn = document.getElementById('zoomIn');
    const zoomOutBtn = document.getElementById('zoomOut');
    const fitBtn = document.getElementById('fit');

    if (zoomInBtn) zoomInBtn.addEventListener('click', function () {
        family.zoom(true);
    });

    if (zoomOutBtn) zoomOutBtn.addEventListener('click', function () {
        family.zoom(false);
    });

    if (fitBtn) fitBtn.addEventListener('click', function () {
        family.fit();
    });
}

document.addEventListener('DOMContentLoaded', function () {
    loadTree().then(nodes => {
        if (nodes && nodes.length > 0) {
            console.log(`Загружено ${nodes.length} узлов`);
            setupEventListeners();
            family.on('field', function (sender, args) {
                if (args.name === 'born') {
                    let date = new Date(args.value);
                    args.value = date.toLocaleDateString();
                }
            });
            family.onUpdateNode(async (args) => {
                console.log(args)
                let node;
                if (args.addNodesData && args.addNodesData.length > 0) {
                    node = args.addNodesData[0];
                    if (node.pids || node.fid || node.mid){
                        await addNode(node)
                    }
                }
                if (args.addNodesData.length === 0 && args.updateNodesData && args.updateNodesData.length > 0) {
                    node = args.updateNodesData[0];
                    console.log(node)
                    const savedNode = await updateNode(node, `${API}/persons/${node.id}`);

                }



            })
        }
    });
});


async function addNode(node) {
    try {
        const response = await fetch(`${API}/persons`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: node.name || 'Новый человек',
                gender: node.gender || 'male',
                birthDate: node.born ? new Date(node.born).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
                photo: node.photo || '',
                fatherId: node.fid || null,
                motherId: node.mid || null,
                spouseId: node.pids?.[0] || null,
            })
        });

        let data = await response.json();
        data = cleanParentIds(data.nodes)
        console.log("data", data)
        if (data && Array.isArray(data)) {
            family.load(data);
        } else {
            console.warn('Неверный формат ответа:', data);
        }

    } catch (error) {
        console.error('Ошибка добавления узла:', error);
    }
}

async function updateNode(node, url) {
    try {
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                    id: node.id,
                    name: node.name || 'Новый человек',
                    gender: node.gender || 'male',
                    birthDate: node.born ? new Date(node.born).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
                    photo: node.photo || '',
                    fatherId: node.fid || null,
                    motherId: node.mid || null,
                    spouseId: node.pids?.[0] || null,
                }
            )
        });

        const data = await response.json();

        if (data.nodes && Array.isArray(data.nodes)) {
            console.log("Узел успешно обновлен")
        } else {
            console.warn('Неверный формат ответа:', data);
        }

    } catch (error) {
        console.error('Ошибка обновления:', error);
    }
}

async function deleteNode(nodeId) {
    try {
        const response = await fetch(`${API}/persons/${nodeId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            const treeData = await fetch(`${API}/tree`).then(r => r.json());
            family.load(cleanParentIds(treeData.nodes));
        }
    } catch (error) {
        alert('Ошибка сервера');
    }
}

function cleanParentIds(nodes) {
    if (!Array.isArray(nodes)) {
        console.warn('cleanParentIds: ожидается массив');
        return [];
    }

    return nodes.map((node, index) => {
        if (!node || typeof node !== 'object') {
            console.warn(`cleanParentIds: некорректный узел на позиции ${index}`);
            return node;
        }

        const cleaned = {...node};

        cleaned.mid = cleaned.mid === 0 ? null : cleaned.mid;
        cleaned.fid = cleaned.fid === 0 ? null : cleaned.fid;

        return cleaned;
    });
}



