import React from "react";
import Styles from "./mapCollection.module.css"
import MapItem from "./mapItem";
import NewMapItemSelector from "./newMapItemSelector";

import Disclosure from "./disclosure.svg";
import Disclosed from "./disclosed.svg";

class MapCollection extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            dragging: false,
            collapsed: false
        };
    }

    static collectionName(collection) {
        return collection || "No Collection";
    }

    static collectionLinkName(collection) {
        return this.collectionName(collection).replace(" ", "-");
    }

    render() {
        return <div className={Styles.CollectionRoot} onDragEnter={this.dragEnter.bind(this)} onDrop={this.drop.bind(this)} onDragLeave={this.dragLeave.bind(this)} onDragOver={this.dragOver.bind(this)}>
            <div className={Styles.CollectionHeader} onClick={this.toggleCollapse.bind(this)}>
                <img className={Styles.Disclosure} src={this.state.collapsed ? Disclosure : Disclosed}/>
                <a name={MapCollection.collectionLinkName(this.props.collection)} />
                {MapCollection.collectionName(this.props.collection)}
            </div>
            {this.renderMaps()}
        </div>
    }

    renderMaps() {
        if (this.state.collapsed) return null;

        return <div className={[Styles.CollectionItems, this.state.dragging ? Styles.Dragging : ""].join(" ")}>
                {this.props.data.map(map => <MapItem isCollection={true} collection={this.props.collection} manager={this.props.manager} key={map.id} data={map} />)}
        </div>
    }

    toggleCollapse() {
        this.setState((state) => ({
            collapsed: !state.collapsed
        }));
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
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: true
        });
    }

    drop(e) {
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: false
        });

        let transfer = e.dataTransfer;

        if (transfer.types.includes("application/x.vicr123.iomfrontend.mapid")) {
            //Adding to collection
            this.props.manager.addToCollection(transfer.getData("application/x.vicr123.iomfrontend.mapid"), this.props.collection);
        }
    }
}

export default MapCollection;