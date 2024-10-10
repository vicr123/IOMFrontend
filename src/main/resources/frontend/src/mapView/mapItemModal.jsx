import Styles from "./mapItemModal.module.css"
import GeneratedMap from "./generatedmap.svg";
import {ControlButton} from "./controlButton";
import {Icon} from "../icon";

export function MapItemModal({open, close, data, manager, isCollection, collection, width, height}) {
    if (!open) return null;

    const get = () => {
        manager.giveMap(data.id);
        close();
    };
    const recategorise = async () => {
        let catg = prompt("Name of category for this map:", data.category);
        if (catg !== null) {
            await manager.setCatg(data.id, catg);
            close();
        }
    };
    const addToCollection = async () => {
        let collection = prompt("Name of collection to add to:", data.category);
        if (collection !== null) {
            await manager.addToCollection(data.id, collection);
            close();
        }
    }
    const removeFromCollection = async () => {
        if (window.confirm("Remove this map from the collection? Any placed maps will stay active.")) {
            await manager.deleteCollection(data.id, collection);
            close();
        }
    }
    const remove = async () => {
        if (window.confirm("Delete this map? Any mapsigns remaining on the world will cease to function correctly, and the map will be deleted from all public collections as well.")) {
            await manager.deleteMap(data.id);
            close();
        }
    }
    const rename = async () => {
        let name = prompt("Name of this map:", data.name);
        if (name !== null) await manager.setName(data.id, name);
    }

    return <div className={Styles.modal} onClick={close}>
        <div className={Styles.modalContainer} onClick={e => e.stopPropagation()}>
            <img draggable={true}
                 src={data.pictureResource === "x" ? GeneratedMap : `/images/${data.pictureResource}`}
                 className={Styles.image}
            />
            <div className={Styles.name}>
                <span className={Styles.nameString}>{data.name}</span>
                <div className={Styles.renameButton} onClick={rename}>
                    <Icon icon={"edit-rename"} />
                </div>
                <span className={Styles.dimensions}>{width} &times; {height}</span>
            </div>
            <div className={Styles.controls}>
                {isCollection && data.isOwner && <ControlButton text="Remove from Global Collection" onClick={removeFromCollection}/>}
                {!isCollection && <>
                    <ControlButton text="Get" onClick={get}/>
                    <ControlButton text="Recategorise" onClick={recategorise}/>
                    <ControlButton text="Add to Global Collection" onClick={addToCollection}/>
                    <ControlButton text="Delete" onClick={remove}/>
                </>}
            </div>
        </div>
    </div>
}