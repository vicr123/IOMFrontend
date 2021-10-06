import React from "react";
import Styles from "./mapItem.module.css";

class MapItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            dragging: false
        };
    }

    render() {
        return <div draggable={true} className={[Styles.MapItem, this.state.dragging ? Styles.Dragging : ""].join(" ")} onDragEnter={this.dragEnter.bind(this)} onDrop={this.drop.bind(this)} onDragLeave={this.dragLeave.bind(this)} onDragOver={this.dragOver.bind(this)} onDragStart={this.dragStart.bind(this)}>
            <img className={Styles.MapImage} src={`/images/${this.props.data.pictureResource}`} />
            <span>{this.props.data.name}</span>
            <div>
                <button onClick={this.giveMap.bind(this)}>Get</button>
                <button onClick={this.setCatg.bind(this)}>Recategorise</button>
                <button onClick={this.setName.bind(this)}>Rename</button>
                <button onClick={this.deleteMap.bind(this)}>Delete</button>
            </div>
        </div>
    }

    async deleteMap() {
        if (window.confirm("Delete this map? Any mapsigns remaining on the world will cease to function correctly.")) {
            await this.props.manager.deleteMap(this.props.data.id);
        }
    }

    async giveMap() {
        await this.props.manager.giveMap(this.props.data.id);
    }

    async setCatg() {
        let catg = prompt("Name of category for this map:", this.props.data.category);
        if (catg !== null) await this.props.manager.setCatg(this.props.data.id, catg);
    }

    async setName() {
        let name = prompt("Name of this map:", this.props.data.name);
        if (name !== null) await this.props.manager.setName(this.props.data.id, name);
    }

    dragStart(e) {
        e.dataTransfer.setData("application/x.vicr123.iomfrontend.mapid", this.props.data.id);
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
                .filter(file => file.type === "image/png")[0];

            if (file) this.props.manager.updateImage(this.props.data.id, file);
        } catch (exception) {
            alert("Couldn't update the map.");
        }
    }
}

export default MapItem;