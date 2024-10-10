import React from "react";
import MapItemDropTarget from "./mapitemdroptarget";
import Toast from "../Toast";
import Styles from "./mapItem.module.css";

import GeneratedMap from "./generatedmap.svg";
import {MapItemModal} from "./mapItemModal";
import {Icon} from "../icon";

class MapItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            dragging: false,
            draggingThis: false,
            width: 0,
            height: 0,
            modalOpen: false
        };
    }

    componentDidMount() {
        let img = new Image();
        img.addEventListener("load", () => {
            this.setState({
                width: Math.ceil(img.naturalWidth / 128),
                height: Math.ceil(img.naturalHeight / 128)
            })
        });
        img.src = this.props.data.pictureResource === "x" ? GeneratedMap : `/images/${this.props.data.pictureResource}`;
    }

    render() {
        if (this.state.width === 0) {
            return null;
        } else {
            return <div className={Styles.MapItemContainer}>
                <div draggable={true}
                     className={[Styles.MapItem, this.state.dragging ? Styles.Dragging : "", Object.keys(this.props.data.rotondos).length === 0 ? "" : Styles.MapItemRotondo].join(" ")}
                     onDragEnter={this.dragEnter.bind(this)}
                     onDrop={this.drop.bind(this)}
                     onDragLeave={this.dragLeave.bind(this)}
                     onDragOver={this.dragOver.bind(this)}
                     onDragStart={this.dragStart.bind(this)}
                     onDragEnd={this.dragEnd.bind(this)}
                     onClick={this.click.bind(this)}>
                    <img className={Styles.mapImage} src={this.props.data.pictureResource === "x" ? GeneratedMap : `/images/${this.props.data.pictureResource}`} />
                </div>
                <div className={Styles.MapItemInfo} onClick={this.expand.bind(this)}>
                    <span>{this.props.data.name}</span>
                    <div style={{flexGrow: "1"}}/>
                    <Icon icon={"application-menu"}/>
                </div>
                <MapItemModal width={this.state.width} height={this.state.height} collection={this.props.collection} isCollection={this.props.isCollection}
                              open={this.state.modalOpen} close={this.collapse.bind(this)} data={this.props.data}
                              manager={this.props.manager}/>
            </div>
        }
    }

    async expand() {
        this.setState({
            modalOpen: true
        })
    }

    async collapse() {
        this.setState({
            modalOpen: false
        })
    }

    async click() {
        if (Object.keys(this.props.data.rotondos).length !== 0) {
            await this.giveRotondoMap(this.props.data.id)
            return;
        }
        // try {
        Toast.makeToast(<Toast title={"Map Obtained"} text={"The map is now in your inventory."} />)
            await this.props.manager.giveMap(this.props.data.id);
        // } catch {
            // Toast.makeToast(<Toast title={"Could not obtain map"} text={"The map could not be obtained."} />)
        // }
    }

    async deleteMap() {
        if (window.confirm("Delete this map? Any mapsigns remaining on the world will cease to function correctly, and the map will be deleted from all public collections as well.")) {
            await this.props.manager.deleteMap(this.props.data.id);
            Toast.makeToast(<Toast title={"Map Deleted"} text={"The map was deleted."} />)
        }
    }

    async removeCollection() {
        if (window.confirm("Remove this map from the collection? Any placed maps will stay active.")) {
            await this.props.manager.deleteCollection(this.props.data.id, this.props.collection);
            Toast.makeToast(<Toast title={"Map removed from collection"} text={"The map was removed from the collection."} />)
        }
    }

    async giveMap() {
        await this.props.manager.giveMap(this.props.data.id);
    }

    async giveRotondoMap(id) {
        Toast.makeToast(<Toast title={"Map Obtained"} text={"The map is now in your inventory."} />)
        await this.props.manager.giveMap(id);
    }

    async setCatg() {
        let catg = prompt("Name of category for this map:", this.props.data.category);
        console.log(catg);
        if (catg !== null) await this.props.manager.setCatg(this.props.data.id, catg);
    }

    async addCollection() {
        let collection = prompt("Name of collection to add to:", this.props.data.category);
        if (collection !== null) await this.props.manager.addToCollection(this.props.data.id, collection);
    }

    async setName() {
        let name = prompt("Name of this map:", this.props.data.name);
        if (name !== null) await this.props.manager.setName(this.props.data.id, name);
    }

    async createRotondo() {
        await this.props.manager.createRotondo(this.props.data.id);
    }

    dragStart(e) {
        e.dataTransfer.setData("application/x.vicr123.iomfrontend.mapid", this.props.data.id);
        e.dataTransfer.setData("application/x.vicr123.iomfrontend.maporigin", this.props.isCollection ? "collection" : "category");
        e.dataTransfer.dropEffect = "move";

        this.setState({
            draggingThis: true
        });
    }

    dragEnd(e) {
        this.setState({
            draggingThis: false
        });
    }

    dragEnter(e) {
        e.preventDefault();
        e.stopPropagation();
        let transfer = e.dataTransfer;
        transfer.dropEffect = "copy";
    }

    dragLeave(e) {
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: false
        });
    }

    dragOver(e) {
        if (e.dataTransfer.types.includes("application/x.vicr123.iomfrontend.mapid")) return;
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: true
        });
    }

    drop(e) {
        if (e.dataTransfer.types.includes("application/x.vicr123.iomfrontend.mapid")) return;
        try {
            e.preventDefault();
            e.stopPropagation();

            this.setState({
                dragging: false
            });

            let transfer = e.dataTransfer;
            let file = [...transfer.items].filter(item => item.kind === "file")
                .map(item => item.getAsFile())
                .filter(file => ["image/png", "image/svg+xml", "image/gif"].includes(file.type))[0];

            if (file) this.props.manager.updateImage(this.props.data.id, file);
        } catch (exception) {
            alert("Couldn't update the map.");
        }
    }
}

export default MapItem;