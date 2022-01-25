import React from "react";
import Styles from "./mapItem.module.css";

import GeneratedMap from "./generatedmap.svg";

class MapItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            dragging: false
        };
    }

    renderButtons() {
        let buttons = [];
        buttons.push(<button onClick={this.giveMap.bind(this)} key={"get"}>Get</button>);

        if (this.props.isCollection) {
            if (this.props.data.isOwner) buttons.push(<button onClick={this.removeCollection.bind(this)} key={"removeFromCollection"}>Remove from Collection</button>)
        } else {
            buttons.push(<button onClick={this.setCatg.bind(this)} key={"recategorise"}>Recategorise</button>);
            buttons.push(<button onClick={this.addCollection.bind(this)} key={"addToCollection"}>Add to Collection</button>);
            buttons.push(<button onClick={this.setName.bind(this)} key={"rename"}>Rename</button>);
            buttons.push(<button onClick={this.deleteMap.bind(this)} key={"delete"}>Delete</button>);
        }

        return buttons;
    }

    render() {
        return <div draggable={true} className={[Styles.MapItem, this.state.dragging ? Styles.Dragging : ""].join(" ")} onDragEnter={this.dragEnter.bind(this)} onDrop={this.drop.bind(this)} onDragLeave={this.dragLeave.bind(this)} onDragOver={this.dragOver.bind(this)} onDragStart={this.dragStart.bind(this)}>
            <img className={Styles.MapImage} src={this.props.data.pictureResource === "x" ? GeneratedMap : `/images/${this.props.data.pictureResource}`} />
            <span>{this.props.data.name}</span>
            <div>
                {this.renderButtons()}
            </div>
        </div>
    }

    async deleteMap() {
        if (window.confirm("Delete this map? Any mapsigns remaining on the world will cease to function correctly, and the map will be deleted from all public collections as well.")) {
            await this.props.manager.deleteMap(this.props.data.id);
        }
    }

    async removeCollection() {
        if (window.confirm("Remove this map from the collection? Any placed maps will stay active.")) {
            await this.props.manager.deleteCollection(this.props.data.id, this.props.collection);
        }
    }

    async giveMap() {
        await this.props.manager.giveMap(this.props.data.id);
    }

    async setCatg() {
        let catg = prompt("Name of category for this map:", this.props.data.category);
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

    dragStart(e) {
        e.dataTransfer.setData("application/x.vicr123.iomfrontend.mapid", this.props.data.id);
        e.dataTransfer.setData("application/x.vicr123.iomfrontend.maporigin", this.props.isCollection ? "collection" : "category");
        e.dataTransfer.dropEffect = "move";
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
                .filter(file => ["image/png", "image/svg+xml"].includes(file.type))[0];

            if (file) this.props.manager.updateImage(this.props.data.id, file);
        } catch (exception) {
            alert("Couldn't update the map.");
        }
    }
}

export default MapItem;