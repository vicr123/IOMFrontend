import React, {useEffect, useState} from "react";
import Toast from "../Toast";
import Styles from "./mapItem.module.css";

import GeneratedMap from "./generatedmap.svg";
import {MapItemModal} from "./mapItemModal";
import {Icon} from "../icon";
import {Item, Menu, useContextMenu} from "react-contexify";
import {availableMapActions} from "./mapActions";

export default function MapItem({data, collection, isCollection, manager}) {
    const [width, setWidth] = useState(0);
    const [height, setHeight] = useState(0);
    const [dragging, setDragging] = useState(false);
    const [draggingThis, setDraggingThis] = useState(false);
    const [modalOpen, setModalOpen] = useState(false);

    const MAP_CONTEXT_MENU = `map-${data.name}-${isCollection}-${collection}`;

    const {show} = useContextMenu({
        id: MAP_CONTEXT_MENU
    });

    useEffect(() => {
        const img = new Image();
        img.addEventListener("load", () => {
            setWidth(Math.ceil(img.naturalWidth / 128));
            setHeight(Math.ceil(img.naturalHeight / 128));
        });
        img.src = data.pictureResource === "x" ? GeneratedMap : `/images/${data.pictureResource}`;
    }, []);

    const dragStart = (e) => {
        e.dataTransfer.setData("application/x.vicr123.iomfrontend.mapid", data.id);
        e.dataTransfer.setData("application/x.vicr123.iomfrontend.maporigin", isCollection ? "collection" : "category");
        e.dataTransfer.dropEffect = "move";

        setDraggingThis(true);
    }

    const dragEnd = (e) => {
        setDraggingThis(false);
    }

    const dragEnter = (e) => {
        e.preventDefault();
        e.stopPropagation();
        let transfer = e.dataTransfer;
        transfer.dropEffect = "copy";
    }

    const dragLeave = (e) => {
        e.preventDefault();
        e.stopPropagation();

        setDragging(false);
    }

    const dragOver = (e) => {
        if (e.dataTransfer.types.includes("application/x.vicr123.iomfrontend.mapid")) return;
        e.preventDefault();
        e.stopPropagation();

        setDragging(true);
    }

    const drop = (e) => {
        if (e.dataTransfer.types.includes("application/x.vicr123.iomfrontend.mapid")) return;
        try {
            e.preventDefault();
            e.stopPropagation();

            setDragging(false);

            let transfer = e.dataTransfer;
            let file = [...transfer.items].filter(item => item.kind === "file")
                .map(item => item.getAsFile())
                .filter(file => ["image/png", "image/svg+xml", "image/gif", "image/webp"].includes(file.type))[0];

            if (file) manager.updateImage(data.id, file);
        } catch (exception) {
            alert("Couldn't update the map.");
        }
    }

    const click = async () => {
        Toast.makeToast(<Toast title={"Map Obtained"} text={"The map is now in your inventory."} />)
        await manager.giveMap(data.id);
    }

    const openContextMenu = (e) => {
        show({
            event: e
        })
    }

    if (width === 0) {
        return null;
    } else {
        return <div className={Styles.MapItemContainer} onContextMenu={openContextMenu}>
            <div draggable={true}
                 className={[Styles.MapItem, dragging ? Styles.Dragging : "", Object.keys(data.rotondos).length === 0 ? "" : Styles.MapItemRotondo].join(" ")}
                 onDragEnter={dragEnter}
                 onDrop={drop}
                 onDragLeave={dragLeave}
                 onDragOver={dragOver}
                 onDragStart={dragStart}
                 onDragEnd={dragEnd}
                 onClick={click}>
                <img className={Styles.mapImage} src={data.pictureResource === "x" ? GeneratedMap : `/images/${data.pictureResource}`} />
            </div>
            <div className={Styles.MapItemInfo} onClick={() => setModalOpen(true)}>
                <span>{data.name}</span>
                <div style={{flexGrow: "1"}}/>
                <Icon icon={"application-menu"}/>
            </div>
            <MapItemModal width={width} height={height} collection={collection} isCollection={isCollection}
                          open={modalOpen} close={() => setModalOpen(false)} data={data}
                          manager={manager}/>
            <Menu id={MAP_CONTEXT_MENU} theme={"dark"}>
                {availableMapActions(data, isCollection, collection, manager, () => {}).map(({text, action}) => <Item onClick={action}>{text}</Item> )}
            </Menu>
        </div>
    }
}
