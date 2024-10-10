import Styles from "./mapItemModal.module.css"
import GeneratedMap from "./generatedmap.svg";
import {ControlButton} from "./controlButton";
import {Icon} from "../icon";
import {availableMapActions} from "./mapActions";

export function MapItemModal({open, close, data, manager, isCollection, collection, width, height}) {
    if (!open) return null;

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
                {availableMapActions(data, isCollection, collection, manager, close).map(({text, action}) => <ControlButton text={text} onClick={action} key={text} />)}
            </div>
        </div>
    </div>
}