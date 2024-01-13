import React from "react";
import MapItemDropTarget from "./mapitemdroptarget";
import Toast from "../Toast";
import Styles from "./mapItem.module.css";

import GeneratedMap from "./generatedmap.svg";

class MapItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            dragging: false,
            draggingThis: false,
            width: 0,
            height: 0
        };
    }

    renderButtons() {
        if (this.state.draggingThis) {
            let buttons = [];

            if (this.props.isCollection) {
                if (this.props.data.isOwner) buttons.push(<MapItemDropTarget onClick={this.removeCollection.bind(this)} key={"removeFromCollection"}>Remove from Collection</MapItemDropTarget>)
            } else {
                buttons.push(<MapItemDropTarget onClick={this.setCatg.bind(this)} key={"recategorise"}>Recategorise</MapItemDropTarget>);
                buttons.push(<MapItemDropTarget onClick={this.addCollection.bind(this)} key={"addToCollection"}>Add to Collection</MapItemDropTarget>);
                if (Object.keys(this.props.data.rotondos).length === 0) {
                    // buttons.push(<MapItemDropTarget onClick={this.createRotondo.bind(this)} key={"rotondo"}>Convert to Rotondo Map</MapItemDropTarget>);
                }
                buttons.push(<MapItemDropTarget onClick={this.setName.bind(this)} key={"rename"}>Rename</MapItemDropTarget>);
                buttons.push(<MapItemDropTarget onClick={this.deleteMap.bind(this)} key={"delete"}>Delete</MapItemDropTarget>);
            }

            return <div className={Styles.buttonSidebar}>
                <span className={Styles.ActionsTitle}>Actions</span>
                {buttons}
            </div>;
        } else {
            return null;
        }
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

    renderRotondoComponents() {
        let rotondos = Object.keys(this.props.data.rotondos);
        if (rotondos.length === 0) return;

        rotondos.push("0");
        let rotondoMap = this.props.data.rotondos;
        rotondoMap["0"] = this.props.data.id;

        const classes = {
            "0": Styles.North,
            "1": Styles.East,
            "2": Styles.South,
            "3": Styles.West
        }

        const rotondoNames = {
            "0": "N",
            "1": "E",
            "2": "S",
            "3": "W"
        }

        return rotondos.map(rotondoType => <div key={rotondoType} className={[classes[rotondoType], Styles.MapItemRotondoItem].join(" ")} onClick={() => this.giveRotondoMap(rotondoMap[rotondoType])}>{rotondoNames[rotondoType]}</div>);
    }

    render() {
        if (this.state.width === 0) {
            return null;
        } else {
            return <>
                <div draggable={true}
                     className={[Styles.MapItem, this.state.dragging ? Styles.Dragging : "", Object.keys(this.props.data.rotondos).length === 0 ? "" : Styles.MapItemRotondo].join(" ")}
                     onDragEnter={this.dragEnter.bind(this)}
                     onDrop={this.drop.bind(this)}
                     onDragLeave={this.dragLeave.bind(this)}
                     onDragOver={this.dragOver.bind(this)}
                     onDragStart={this.dragStart.bind(this)}
                     onDragEnd={this.dragEnd.bind(this)}
                     onClick={this.click.bind(this)}
                     style={{
                         background: `linear-gradient(to right,rgba(0, 0, 0, 0.2),rgba(0, 0, 0, 0.2)),url(${this.props.data.pictureResource === "x" ? GeneratedMap : `/images/${this.props.data.pictureResource}`}) no-repeat center/cover`,
                         aspectRatio: `${Math.min(this.state.width, 5)} / ${Math.min(this.state.height, 5)}`,
                         gridColumn: `span ${Math.min(this.state.width, 5)}`,
                         gridRow: `span ${Math.min(this.state.height, 5)}`,
                         // width: this.state.width * 2,
                         // height: this.state.height * 2
                     }}
                >
                    <div className={Styles.MapItemRotondoSelect}>
                        {this.renderRotondoComponents()}
                    </div>
                    <div className={Styles.MapItemInfo}>
                        <span>{this.state.height} &times; {this.state.width} {Object.keys(this.props.data.rotondos).length === 0 ? "" : "R"}</span>
                        <span>{this.props.data.name}</span>
                    </div>
                </div>
                {this.renderButtons()}
            </>
        }
    }

    async click() {
        if (Object.keys(this.props.data.rotondos).length !== 0) return;
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